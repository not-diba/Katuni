package com.diba.katuni.data.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Handles rendering PDF pages to bitmap images for viewing
 * Supports lazy loading for better performance with large PDFs
 */
object PdfPageRenderer {

    /**
     * Gets the total page count of a PDF without rendering
     */
    fun getPdfPageCount(context: Context, pdfUri: Uri): Int {
        return try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r")
                ?: return 0

            val renderer = PdfRenderer(fileDescriptor)
            val count = renderer.pageCount

            renderer.close()
            fileDescriptor.close()

            count
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Renders a batch of PDF pages (for progressive loading)
     * @param startPage The first page to render (0-indexed)
     * @param count Number of pages to render
     * @return List of file paths for the rendered pages
     */
    suspend fun renderPageBatch(
        context: Context,
        pdfUri: Uri,
        startPage: Int,
        count: Int
    ): List<String> = withContext(Dispatchers.IO) {
        val pages = mutableListOf<String>()

        try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r")
                ?: throw Exception("Could not open PDF file")

            val renderer = PdfRenderer(fileDescriptor)

            // Create cache directory for this PDF
            val pdfHash = pdfUri.toString().hashCode().toString()
            val cacheDir = File(context.cacheDir, "pdf_pages/$pdfHash")
            cacheDir.mkdirs()

            // Calculate end page
            val endPage = minOf(startPage + count, renderer.pageCount)

            // Render each page in the batch
            for (pageIndex in startPage until endPage) {
                val pageFile = File(cacheDir, "page_$pageIndex.png")

                // Skip if already rendered
                if (pageFile.exists()) {
                    pages.add(pageFile.absolutePath)
                    continue
                }

                val page = renderer.openPage(pageIndex)

                // Create bitmap with appropriate size
                // Scale up for better quality (2x resolution)
                val scale = 2.0f
                val width = (page.width * scale).toInt()
                val height = (page.height * scale).toInt()

                val bitmap = createBitmap(width, height)

                // Render page to bitmap
                page.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )

                // Save bitmap to cache
                FileOutputStream(pageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                pages.add(pageFile.absolutePath)

                // Clean up
                bitmap.recycle()
                page.close()
            }

            renderer.close()
            fileDescriptor.close()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        pages
    }

    /**
     * Renders a single page from a PDF
     * Returns the file path if successful, null otherwise
     */
    suspend fun renderSinglePage(
        context: Context,
        pdfUri: Uri,
        pageIndex: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            // Create cache directory
            val pdfHash = pdfUri.toString().hashCode().toString()
            val cacheDir = File(context.cacheDir, "pdf_pages/$pdfHash")
            cacheDir.mkdirs()

            val pageFile = File(cacheDir, "page_$pageIndex.png")

            // Return if already exists
            if (pageFile.exists()) {
                return@withContext pageFile.absolutePath
            }

            val fileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r")
                ?: return@withContext null

            val renderer = PdfRenderer(fileDescriptor)

            if (pageIndex >= renderer.pageCount || pageIndex < 0) {
                renderer.close()
                fileDescriptor.close()
                return@withContext null
            }

            val page = renderer.openPage(pageIndex)

            // Render page
            val scale = 2.0f
            val width = (page.width * scale).toInt()
            val height = (page.height * scale).toInt()
            val bitmap = createBitmap(width, height)

            page.render(
                bitmap,
                null,
                null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )

            // Save to cache
            FileOutputStream(pageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            bitmap.recycle()
            page.close()
            renderer.close()
            fileDescriptor.close()

            pageFile.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets the expected file path for a page (whether rendered or not)
     * Useful for initializing page lists
     */
    fun getPagePath(context: Context, pdfUri: Uri, pageIndex: Int): String {
        val pdfHash = pdfUri.toString().hashCode().toString()
        val cacheDir = File(context.cacheDir, "pdf_pages/$pdfHash")
        return File(cacheDir, "page_$pageIndex.png").absolutePath
    }
}