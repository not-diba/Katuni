package com.diba.katuni.ui.components

import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.diba.katuni.data.file.ThumbnailExtractor
import com.diba.katuni.model.KatuniFile

class ComicThumbnailFetcher(
    private val data: KatuniFile,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        val context = options.context
        val bitmap = ThumbnailExtractor.extractThumbnail(
            context,
            data.path.toUri(),
            data.mimeType
        ) ?: throw Exception("Failed to extract thumbnail")

        return ImageFetchResult(
            image = bitmap.asImage(),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    class Factory : Fetcher.Factory<KatuniFile> {
        override fun create(
            data: KatuniFile,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return ComicThumbnailFetcher(data, options)
        }
    }
}