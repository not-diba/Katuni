package com.diba.katuni.data.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.core.graphics.createBitmap
import java.util.zip.ZipInputStream

object ThumbnailExtractor {

    fun extractThumbnail(
        context: Context,
        uri: Uri,
        mimeType: String?
    ): Bitmap? {
        return try {
            when {
                mimeType?.contains("pdf") == true -> extractPdfThumbnail(context, uri)
                mimeType?.contains("zip") == true -> extractCbzThumbnail(context, uri)
                else -> extractCbzThumbnail(context, uri) // Default to CBZ
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractPdfThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            val fd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return null
            val renderer = PdfRenderer(fd)
            if (renderer.pageCount > 0) {
                val page = renderer.openPage(0)
                val bitmap = createBitmap(page.width, page.height)
                page.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                page.close()
                renderer.close()
                fd.close()
                bitmap
            } else {
                renderer.close()
                fd.close()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractCbzThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (isImageFile(entry.name)) {
                            return BitmapFactory.decodeStream(zip)
                        }
                        entry = zip.nextEntry
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isImageFile(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".webp")
    }
}