package com.savvasdalkitsis.uhuruphotos.image.cache

import coil.disk.DiskCache
import coil.memory.MemoryCache
import javax.inject.Inject

class ImageCacheController @Inject constructor(
    private val memoryCache: MemoryCache,
    private val diskCache: DiskCache,
) {

    fun clear() {
        memoryCache.clear()
        diskCache.clear()
    }
}