package com.savvasdalkitsis.uhuruphotos.settings.usecase

import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.savvasdalkitsis.uhuruphotos.video.api.VideoCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import okhttp3.Cache
import javax.inject.Inject

internal class CacheUseCase @Inject constructor(
    private val diskCache: DiskCache,
    private val memoryCache: MemoryCache,
    @VideoCache
    private val videoDiskCache: Cache,
) {

    private val imageDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val imageMemCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val videoDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)

    fun observeImageDiskCacheCurrentUse(): Flow<Int> = imageDiskCacheFlow.onStart {
        updateCurrentImageDiskCacheFlow()
    }

    fun observeImageMemCacheCurrentUse(): Flow<Int> = imageMemCacheFlow.onStart {
        updateCurrentImageMemCacheFlow()
    }

    fun observeVideoDiskCacheCurrentUse(): Flow<Int> = videoDiskCacheFlow.onStart {
        updateCurrentVideoDiskCacheFlow()
    }

    suspend fun clearImageDiskCache() {
        withContext(Dispatchers.IO) {
            diskCache.clear()
            updateCurrentImageDiskCacheFlow()
        }
    }

    suspend fun clearImageMemCache() {
        withContext(Dispatchers.IO) {
            memoryCache.clear()
            updateCurrentImageMemCacheFlow()
        }
    }

    suspend fun clearVideoDiskCache() {
        withContext(Dispatchers.IO) {
            runCatching {
                videoDiskCache.evictAll()
            }
            updateCurrentVideoDiskCacheFlow()
        }
    }

    private fun updateCurrentImageDiskCacheFlow() = imageDiskCacheFlow.tryEmit(diskCache.size.mb)
    private fun updateCurrentImageMemCacheFlow() = imageMemCacheFlow.tryEmit(memoryCache.size.mb)
    private fun updateCurrentVideoDiskCacheFlow() = videoDiskCacheFlow.tryEmit(videoDiskCache.size().mb)

    private val Number.mb: Int get() = toInt() / 1024 / 1024
}