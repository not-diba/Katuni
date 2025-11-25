package com.diba.katuni

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.diba.katuni.data.AppContainer
import com.diba.katuni.data.AppContainerImpl
import com.diba.katuni.ui.components.ComicThumbnailFetcher

class KatuniApplication : Application(), SingletonImageLoader.Factory {
    companion object {
        lateinit var container: AppContainer
            private set
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(applicationContext)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(ComicThumbnailFetcher.Factory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(this, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            .crossfade(true)
            .build()
    }
}