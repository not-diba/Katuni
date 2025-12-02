package com.diba.katuni.ui.screens.comic_viewer

data class ComicViewerUiState(
    val pages: List<String> = emptyList(),
    val totalPages: Int = 0,
    val loadedPages: Int = 0,
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val comicName: String = ""
)
