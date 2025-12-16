package com.diba.katuni.ui.screens.comic_viewer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diba.katuni.data.file.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.diba.katuni.data.Result

class ComicViewerViewModel(
    private val repository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComicViewerUiState())
    val uiState: StateFlow<ComicViewerUiState> = _uiState.asStateFlow()

    // Store context for background loading
    private var appContext: Context? = null

    companion object {
        private const val BATCH_SIZE = 10 // Load 10 pages at a time
        private const val PRELOAD_THRESHOLD = 3 // Start loading next batch when 3 pages away
    }

    fun loadComic(context: Context, comicPath: String, comicName: String) {
        // Store application context for background operations
        appContext = context.applicationContext

        viewModelScope.launch {
            _uiState.value = ComicViewerUiState(
                isLoading = true,
                comicName = comicName,
                comicPath = comicPath
            )

            // Determine file type
            val fileType = when {
                comicPath.lowercase().endsWith(".pdf") -> FileType.PDF
                comicPath.lowercase().endsWith(".cbz") ||
                        comicPath.lowercase().endsWith(".zip") -> FileType.CBZ
                else -> FileType.UNKNOWN
            }

            when (val result = repository.getComicPages(context, comicPath)) {
                is Result.Success -> {
                    _uiState.value = ComicViewerUiState(
                        pages = result.data,
                        totalPages = result.data.size,
                        isLoading = false,
                        comicName = comicName,
                        comicPath = comicPath,
                        fileType = fileType
                    )

                    // Load first batch immediately for both types
                    loadPageBatch(0)
                }

                is Result.Error -> {
                    _uiState.value = ComicViewerUiState(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to load comic",
                        comicName = comicName,
                        comicPath = comicPath,
                        fileType = fileType
                    )
                }
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)

        // Check if we need to preload next batch
        checkAndLoadNextBatch(page)
    }

    private fun checkAndLoadNextBatch(currentPage: Int) {
        val state = _uiState.value

        // Calculate which batch this page belongs to
        val currentBatch = currentPage / BATCH_SIZE
        val nextBatch = currentBatch + 1
        val nextBatchStart = nextBatch * BATCH_SIZE

        // Check if we're close to the next batch
        val distanceToNextBatch = nextBatchStart - currentPage

        if (distanceToNextBatch <= PRELOAD_THRESHOLD) {
            // Check if next batch is already loaded
            val nextBatchRange = nextBatchStart until (nextBatchStart + BATCH_SIZE)
            val isNextBatchLoaded = state.loadedPageRanges.any { range ->
                nextBatchRange.all { it in range }
            }

            println("Page $currentPage: distance to next batch ($nextBatchStart) = $distanceToNextBatch, loaded = $isNextBatchLoaded")

            if (!isNextBatchLoaded && nextBatchStart < state.totalPages) {
                println("Triggering load for next batch starting at page $nextBatchStart")
                // Load next batch in background
                viewModelScope.launch {
                    loadPageBatch(nextBatchStart)
                }
            }
        }
    }

    private suspend fun loadPageBatch(startPage: Int) {
        val ctx = appContext ?: return
        val state = _uiState.value

        // Check if already loaded
        val batchRange = startPage until minOf(startPage + BATCH_SIZE, state.totalPages)
        if (state.loadedPageRanges.any { range -> batchRange.all { it in range } }) {
            println("Batch $startPage-${batchRange.last} already loaded, skipping")
            return // Already loaded
        }

        println("Loading batch: pages $startPage-${batchRange.last} for ${state.fileType}")

        val result = when (state.fileType) {
            FileType.PDF -> repository.renderPdfPageBatch(
                ctx,
                state.comicPath,
                startPage,
                BATCH_SIZE
            )
            FileType.CBZ -> repository.extractCbzPageBatch(
                ctx,
                state.comicPath,
                startPage,
                BATCH_SIZE
            )
            FileType.UNKNOWN -> return
        }

        when (result) {
            is Result.Success -> {
                // Mark this range as loaded by adding to the mutable set
                val updatedRanges = state.loadedPageRanges.toMutableSet()
                updatedRanges.add(batchRange)
                _uiState.value = state.copy(loadedPageRanges = updatedRanges)
                println("Successfully loaded batch $startPage-${batchRange.last}, total loaded ranges: ${updatedRanges.size}")
            }

            is Result.Error -> {
                // Log error but don't show to user (pages will show loading state)
                println("Failed to load batch starting at $startPage: ${result.exception.message}")
            }
        }
    }
}