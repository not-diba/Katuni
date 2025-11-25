package com.diba.katuni.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diba.katuni.KatuniApplication

class LibraryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = KatuniApplication.container.fileRepository
        return LibraryScreenViewModel(repository) as T
    }
}