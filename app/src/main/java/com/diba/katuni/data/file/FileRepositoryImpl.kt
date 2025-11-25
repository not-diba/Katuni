package com.diba.katuni.data.file

import android.content.Context
import android.net.Uri
import com.diba.katuni.data.Result
import com.diba.katuni.model.KatuniFile
import androidx.documentfile.provider.DocumentFile


class FileRepositoryImpl : FileRepository {

    override suspend fun getFiles(
        context: Context,
        uri: Uri
    ): Result<List<KatuniFile>> {
        return try {
            val docFile = DocumentFile.fromTreeUri(context, uri)
                ?: return Result.Error(Exception("Invalid folder selected"))

            val files = docFile.listFiles()
                .filter { it.isFile && isComicFile(it.name) }
                .map { df ->
                    KatuniFile(
                        name = df.name ?: "Unknown",
                        modifiedAt = df.lastModified(),
                        size = df.length(),
                        path = df.uri.toString(),
                        mimeType = df.type
                    )
                }

            Result.Success(files)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun isComicFile(name: String?): Boolean {
        if (name == null) return false
        val lower = name.lowercase()
        return lower.endsWith(".cbz") ||
                lower.endsWith(".cbr") ||
                lower.endsWith(".pdf") ||
                lower.endsWith(".zip")
    }
}