package com.diba.katuni.data.file

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.diba.katuni.data.Result
import com.diba.katuni.model.KatuniFile
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream


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

    override suspend fun getComicPages(
        context: Context,
        comicPath: String
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val pages = mutableListOf<Pair<String, String>>() // name, path

            context.contentResolver.openInputStream(comicPath.toUri())?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry

                    while (entry != null) {
                        val entryName = entry.name
                        val lowerName = entryName.lowercase()

                        // Check if it's an image file
                        if (!entry.isDirectory &&
                            (lowerName.endsWith(".jpg") ||
                                    lowerName.endsWith(".jpeg") ||
                                    lowerName.endsWith(".png") ||
                                    lowerName.endsWith(".webp") ||
                                    lowerName.endsWith(".bmp"))) {

                            // Create cache directory for this comic
                            val comicHash = comicPath.hashCode().toString()
                            val cacheDir = File(
                                context.cacheDir,
                                "comic_pages/$comicHash"
                            )
                            cacheDir.mkdirs()

                            // Save page to cache
                            val pageFile = File(cacheDir, entryName)
                            pageFile.parentFile?.mkdirs()

                            FileOutputStream(pageFile).use { output ->
                                zip.copyTo(output)
                            }

                            pages.add(entryName to pageFile.absolutePath)
                        }

                        entry = zip.nextEntry
                    }
                }
            }

            // Sort pages naturally (handles page1, page2, ..., page10 correctly)
            val sortedPages = pages.sortedWith(
                compareBy { it.first.naturalOrder() }
            ).map { it.second }

            Result.Success(sortedPages)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// Helper extension for natural sorting
private fun String.naturalOrder(): String {
    return this.replace(Regex("\\d+")) { matchResult ->
        matchResult.value.padStart(10, '0')
    }
}