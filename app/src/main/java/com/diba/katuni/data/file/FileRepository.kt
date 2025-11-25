package com.diba.katuni.data.file

import android.content.Context
import android.net.Uri
import com.diba.katuni.data.Result
import com.diba.katuni.model.KatuniFile


interface FileRepository {
    suspend fun getFiles(context: Context, uri: Uri): Result<List<KatuniFile>>

//    fun observeFavourites(): Flow<Set<String>>
//
//    suspend fun toggleFavourite(fileUri: String)
}