package com.diba.katuni.ui.screens.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diba.katuni.data.Result
import com.diba.katuni.data.file.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryScreenViewModel(
    private val repository: FileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryScreenUiState())

    val uiState: StateFlow<LibraryScreenUiState> = _uiState.asStateFlow()

    fun loadFilesFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = repository.getFiles(context, uri)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    comics = result.data,
                    isLoading = false,
                    error = null
                )

                is Result.Error -> _uiState.value = _uiState.value.copy(
                    comics = emptyList(),
                    isLoading = false,
                    error = result.exception.message
                )
            }
        }
    }
}