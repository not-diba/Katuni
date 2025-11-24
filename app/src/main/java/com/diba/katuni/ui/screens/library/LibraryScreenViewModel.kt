package com.diba.katuni.ui.screens.library

import androidx.lifecycle.ViewModel
import com.diba.katuni.data.allComics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryScreenViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(
        LibraryScreenUiState(
            comics = allComics.toList()
        )
    )

    val uiState: StateFlow<LibraryScreenUiState> = _uiState.asStateFlow()
}