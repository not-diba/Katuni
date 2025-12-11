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
            val uri = comicPath.toUri()
            val mimeType = context.contentResolver.getType(uri)

            // Check if it's a PDF
            if (mimeType?.contains("pdf") == true || comicPath.lowercase().endsWith(".pdf")) {
                // For PDFs, get page count and create placeholder paths
                val pageCount = PdfPageRenderer.getPdfPageCount(context, uri)
                if (pageCount == 0) {
                    return@withContext Result.Error(Exception("Invalid PDF or no pages found"))
                }

                // Return placeholder paths - pages will be rendered on demand
                val pagePaths = (0 until pageCount).map { pageIndex ->
                    PdfPageRenderer.getPagePath(context, uri, pageIndex)
                }

                return@withContext Result.Success(pagePaths)
            } else {
                // For CBZ/ZIP, get the list of image entries first (fast)
                // Then extract them progressively
                return@withContext getCbzPageList(context, comicPath)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Gets the list of image files in a CBZ without extracting them
     * Returns placeholder paths that will be extracted on demand
     */
    private suspend fun getCbzPageList(
        context: Context,
        comicPath: String
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val imageEntries = mutableListOf<String>()

            // First pass: collect all image entry names
            context.contentResolver.openInputStream(comicPath.toUri())?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry

                    while (entry != null) {
                        if (!entry.isDirectory && isImageFile(entry.name)) {
                            imageEntries.add(entry.name)
                        }
                        entry = zip.nextEntry
                    }
                }
            }

            // Sort entries naturally
            val sortedEntries = imageEntries.sortedWith(
                compareBy { it.naturalOrder() }
            )

            // Create placeholder paths
            val comicHash = comicPath.hashCode().toString()
            val cacheDir = File(context.cacheDir, "comic_pages/$comicHash")

            val pagePaths = sortedEntries.map { entryName ->
                File(cacheDir, entryName).absolutePath
            }

            Result.Success(pagePaths)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Renders a batch of PDF pages progressively
     */
    override suspend fun renderPdfPageBatch(
        context: Context,
        comicPath: String,
        startPage: Int,
        count: Int
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val uri = comicPath.toUri()
            val renderedPages = PdfPageRenderer.renderPageBatch(context, uri, startPage, count)
            Result.Success(renderedPages)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Extracts a batch of CBZ pages progressively
     */
    override suspend fun extractCbzPageBatch(
        context: Context,
        comicPath: String,
        startPage: Int,
        count: Int
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val comicHash = comicPath.hashCode().toString()
            val cacheDir = File(context.cacheDir, "comic_pages/$comicHash")
            cacheDir.mkdirs()

            // Get sorted list of all image entries
            val allEntries = mutableListOf<String>()
            context.contentResolver.openInputStream(comicPath.toUri())?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && isImageFile(entry.name)) {
                            allEntries.add(entry.name)
                        }
                        entry = zip.nextEntry
                    }
                }
            }

            val sortedEntries = allEntries.sortedWith(compareBy { it.naturalOrder() })

            // Determine which entries to extract in this batch
            val endPage = minOf(startPage + count, sortedEntries.size)
            val entriesToExtract = sortedEntries.subList(startPage, endPage)

            val extractedPaths = mutableListOf<String>()

            // Second pass: extract only the needed entries
            context.contentResolver.openInputStream(comicPath.toUri())?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry

                    while (entry != null) {
                        if (entry.name in entriesToExtract) {
                            val pageFile = File(cacheDir, entry.name)

                            // Skip if already extracted
                            if (!pageFile.exists()) {
                                pageFile.parentFile?.mkdirs()
                                FileOutputStream(pageFile).use { output ->
                                    zip.copyTo(output)
                                }
                            }

                            extractedPaths.add(pageFile.absolutePath)
                        }
                        entry = zip.nextEntry
                    }
                }
            }

            // Sort the extracted paths to match the original order
            val sortedPaths = extractedPaths.sortedWith(
                compareBy { File(it).name.naturalOrder() }
            )

            Result.Success(sortedPaths)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun isImageFile(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".webp") ||
                lower.endsWith(".bmp")
    }
}

// Helper extension for natural sorting
private fun String.naturalOrder(): String {
    return this.replace(Regex("\\d+")) { matchResult ->
        matchResult.value.padStart(10, '0')
    }
}