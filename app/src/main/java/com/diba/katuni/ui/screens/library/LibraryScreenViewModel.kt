package com.diba.katuni.ui.screens.library

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diba.katuni.data.PreferencesRepository
import com.diba.katuni.data.Result
import com.diba.katuni.data.file.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import kotlinx.coroutines.flow.first

class LibraryScreenViewModel(
    private val repository: FileRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryScreenUiState())
    val uiState: StateFlow<LibraryScreenUiState> = _uiState.asStateFlow()

    init {
        loadCachedData()
    }

    private fun loadCachedData() {
        viewModelScope.launch {
            val cachedFiles = preferencesRepository.getCachedFiles().first()
            val savedUri = preferencesRepository.getComicsFolder().first()

            if (cachedFiles.isNotEmpty()) {
                _uiState.value = LibraryScreenUiState(
                    comics = cachedFiles,
                    hasFolder = savedUri != null,
                    isLoading = false
                )
            } else if (savedUri != null) {
                _uiState.value = _uiState.value.copy(hasFolder = true)
            }
        }
    }

    fun refreshFiles(context: Context) {
        viewModelScope.launch {
            val savedUri = preferencesRepository.getComicsFolder().first()
            if (savedUri != null) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
                loadFilesFromUri(context, savedUri.toUri(), isRefresh = true)
            }
        }
    }

    fun loadFilesFromUri(context: Context, uri: Uri, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to persist permissions: ${e.message}"
                )
                return@launch
            }

            preferencesRepository.saveComicsFolder(uri.toString())

            when (val result = repository.getFiles(context, uri)) {
                is Result.Success -> {
                    preferencesRepository.cacheFiles(result.data)

                    _uiState.value = LibraryScreenUiState(
                        comics = result.data,
                        isLoading = false,
                        hasFolder = true
                    )
                }

                is Result.Error -> {
                    _uiState.value = LibraryScreenUiState(
                        isLoading = false,
                        error = result.exception.message,
                        hasFolder = true
                    )
                }
            }
        }
    }

    fun clearFolder() {
        viewModelScope.launch {
            preferencesRepository.clearComicsFolder()
            _uiState.value = LibraryScreenUiState()
        }
    }
}