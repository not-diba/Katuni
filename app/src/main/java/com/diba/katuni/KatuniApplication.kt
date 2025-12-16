package com.diba.katuni

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.diba.katuni.data.AppContainer
import com.diba.katuni.data.AppContainerImpl
import com.diba.katuni.data.file.ThumbnailFetcher

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
        return ImageLoader.Builder(context)
            .components {
                add(ThumbnailFetcher.Factory())
            }.memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.30)
                    .strongReferencesEnabled(true)
                    .build()
            }.diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }.logger(DebugLogger())
            .crossfade(true)
            .build()
    }
}