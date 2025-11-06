package com.diba.katuni.data

import com.diba.katuni.data.file.FileRepository
import com.diba.katuni.data.file.FileRepositoryImpl


interface AppContainer {
    val fileRepository: FileRepository
}
class AppContainerImpl: AppContainer {
    override val fileRepository: FileRepository by lazy {
        FileRepositoryImpl();
    }
}