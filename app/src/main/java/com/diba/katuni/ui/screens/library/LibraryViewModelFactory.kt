package com.diba.katuni.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diba.katuni.KatuniApplication

class LibraryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = KatuniApplication.container
        return LibraryScreenViewModel(
            repository = container.fileRepository,
            preferencesRepository = container.preferencesRepository
        ) as T
    }
}