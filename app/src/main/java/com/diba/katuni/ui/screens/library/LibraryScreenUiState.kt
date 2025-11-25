package com.diba.katuni.ui.screens.library

import com.diba.katuni.model.KatuniFile

data class LibraryScreenUiState(
    val comics: List<KatuniFile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)