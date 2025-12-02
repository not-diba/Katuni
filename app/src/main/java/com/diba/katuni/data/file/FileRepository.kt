package com.diba.katuni.data.file

import android.content.Context
import android.net.Uri
import com.diba.katuni.data.Result
import com.diba.katuni.model.KatuniFile


interface FileRepository {
    suspend fun getFiles(context: Context, uri: Uri): Result<List<KatuniFile>>
    suspend fun getComicPages(
        context: Context,
        comicPath: String
    ): Result<List<String>>
}