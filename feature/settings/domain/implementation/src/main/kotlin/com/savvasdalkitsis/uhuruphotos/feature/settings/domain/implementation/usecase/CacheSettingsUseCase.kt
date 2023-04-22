/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.implementation.usecase

import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.CacheSettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.evictAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CacheSettingsUseCase @Inject constructor(
    private val diskCache: DiskCache,
    private val memoryCache: MemoryCache,
    private val videoCache: CacheDataSource.Factory,
) : CacheSettingsUseCase {

    private val imageDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val imageMemCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val videoDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)

    override fun observeImageDiskCacheCurrentUse(): Flow<Int> = imageDiskCacheFlow.onStart {
        updateCurrentImageDiskCacheFlow()
    }

    override fun observeImageMemCacheCurrentUse(): Flow<Int> = imageMemCacheFlow.onStart {
        updateCurrentImageMemCacheFlow()
    }

    override fun observeVideoDiskCacheCurrentUse(): Flow<Int> = videoDiskCacheFlow.onStart {
        updateCurrentVideoDiskCacheFlow()
    }

    override suspend fun clearImageDiskCache() {
        withContext(Dispatchers.IO) {
            diskCache.clear()
            updateCurrentImageDiskCacheFlow()
        }
    }

    override suspend fun clearImageMemCache() {
        withContext(Dispatchers.IO) {
            memoryCache.clear()
            updateCurrentImageMemCacheFlow()
        }
    }

    override suspend fun clearVideoDiskCache() {
        withContext(Dispatchers.IO) {
            runCatching {
                videoCache.evictAll()
            }
            updateCurrentVideoDiskCacheFlow()
        }
    }

    private fun updateCurrentImageDiskCacheFlow() = imageDiskCacheFlow.tryEmit(diskCache.size.mb)
    private fun updateCurrentImageMemCacheFlow() = imageMemCacheFlow.tryEmit(memoryCache.size.mb)
    private fun updateCurrentVideoDiskCacheFlow() = videoDiskCacheFlow.tryEmit(
        (videoCache.cache?.cacheSpace ?: 0).mb
    )

    private val Number.mb: Int get() = toInt() / 1024 / 1024
}
