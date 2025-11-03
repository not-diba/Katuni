package com.diba.katuni.data.file

import com.diba.katuni.data.Result
import com.diba.katuni.model.KatuniFile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FileRepository {
    suspend fun getFiles(parentFile: File): Result<List<KatuniFile>>

    fun observeFavourites(): Flow<Set<String>>

    suspend fun toggleFavourite(fileUri: String)
}