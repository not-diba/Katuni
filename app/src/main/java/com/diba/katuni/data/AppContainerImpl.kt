package com.diba.katuni.data

import android.content.Context
import com.diba.katuni.data.file.FileRepository
import com.diba.katuni.data.file.FileRepositoryImpl

interface AppContainer {
    val fileRepository: FileRepository
    val preferencesRepository: PreferencesRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {
    override val fileRepository: FileRepository by lazy {
        FileRepositoryImpl()
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepositoryImpl(context)
    }
}