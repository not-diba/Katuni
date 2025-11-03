package com.diba.katuni.data.file

import com.diba.katuni.data.Result
import com.diba.katuni.model.KatuniFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class FileRepositoryImpl : FileRepository {
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getFiles(parentFile: File): com.diba.katuni.data.Result<List<KatuniFile>> {
        val katuniFiles = mutableListOf<KatuniFile>()

        val allFiles = parentFile.listFiles()

        if (allFiles.isNullOrEmpty()) {
            return Result.Error(IllegalArgumentException("No files found"))
        }

        allFiles.forEach { file ->
            file.apply {
                if (isDirectory && !isHidden) {
                    getFiles(this)
                } else {
                    // TODO: Maybe filter files to only accept (comics: cbz, epub, pdf)
                    // If you want to filter particular types of files like the pdf|txt|jpg, then with the
                    // following check, you can check the file extension or multiple kinds of extensions
                    // if (name.endsWith(".ext")) {
                    // }
                    katuniFiles.add(
                        KatuniFile(
                            name = name,
                            modifiedAt = lastModified(),
                            size = length(),
                            path = absolutePath
                        )
                    )
                }
            }
        }

        return Result.Success(katuniFiles)
    }

    override fun observeFavourites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavourite(fileUri: String) {
        val set = favorites.value.toMutableSet()
        if (!set.add(fileUri)) {
            set.remove(fileUri)
        }
        favorites.value = set.toSet()
    }
}