package com.diba.katuni.ui.screens.comic_viewer

import android.content.Context
import androidx.core.net.toUri
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

    fun loadComic(context: Context, comicPath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                comicName = comicPath.toUri().lastPathSegment ?: "Comic"
            )

            when (val result = repository.getComicPages(context, comicPath)) {
                is Result.Success -> {
                    _uiState.value = ComicViewerUiState(
                        pages = result.data,
                        totalPages = result.data.size,
                        currentPage = 0,
                        isLoading = false,
                        comicName = comicPath.toUri().lastPathSegment ?: "Comic"
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to load comic"
                    )
                }
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
    }
}