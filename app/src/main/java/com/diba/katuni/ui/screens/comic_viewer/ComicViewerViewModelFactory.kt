package com.diba.katuni.ui.screens.comic_viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diba.katuni.KatuniApplication

class ComicViewerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComicViewerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComicViewerViewModel(
                repository = KatuniApplication.container.fileRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}