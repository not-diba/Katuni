package com.diba.katuni.ui.screens.comic_viewer

data class ComicViewerUiState(
    val pages: List<String> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val comicName: String = "",
    val comicPath: String = "",
    val fileType: FileType = FileType.UNKNOWN,
    val loadedPageRanges: MutableSet<IntRange> = mutableSetOf() // Changed to MutableSet
)

enum class FileType {
    PDF, CBZ, UNKNOWN
}
