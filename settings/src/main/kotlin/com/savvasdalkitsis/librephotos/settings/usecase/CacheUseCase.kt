package com.savvasdalkitsis.librephotos.settings.usecase

import coil.disk.DiskCache
import coil.memory.MemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CacheUseCase @Inject constructor(
    private val diskCache: DiskCache,
    private val memoryCache: MemoryCache,
) {

    private val diskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val memCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)

    fun observeDiskCacheCurrentUse(): Flow<Int> = diskCacheFlow.onStart {
        updateCurrentDiskCacheFlow()
    }

    fun observeMemCacheCurrentUse(): Flow<Int> = memCacheFlow.onStart {
        updateCurrentMemCacheFlow()
    }

    suspend fun clearDiskCache() {
        withContext(Dispatchers.IO) {
            diskCache.clear()
            updateCurrentDiskCacheFlow()
        }
    }

    suspend fun clearMemCache() {
        withContext(Dispatchers.IO) {
            memoryCache.clear()
            updateCurrentMemCacheFlow()
        }
    }

    private fun updateCurrentDiskCacheFlow() = diskCacheFlow.tryEmit(diskCache.size.mb)
    private fun updateCurrentMemCacheFlow() = memCacheFlow.tryEmit(memoryCache.size.mb)

    private val Number.mb: Int get() = toInt() / 1024 / 1024
}