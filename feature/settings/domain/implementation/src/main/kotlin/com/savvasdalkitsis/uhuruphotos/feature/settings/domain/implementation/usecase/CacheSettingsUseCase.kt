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
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.LIGHTBOX_PHOTO_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.LIGHTBOX_PHOTO_MEMORY
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.THUMBNAIL_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.THUMBNAIL_MEMORY
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.VIDEO_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.CacheSettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.evictAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class CacheSettingsUseCase @Inject constructor(
    private val lightboxPhotoDiskCache: DiskCache,
    private val lightboxPhotoMemoryCache: MemoryCache,
    private val videoCache: CacheDataSource.Factory,
) : CacheSettingsUseCase {

    private val lightboxPhotoDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val lightboxPhotoMemCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val thumbnailDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val thumbnailMemCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)
    private val videoDiskCacheFlow: MutableSharedFlow<Int> = MutableSharedFlow(1)

    private val fresco get() = Fresco.getImagePipeline()

    override fun observeCacheCurrentUse(cacheType: CacheType): Flow<Int> = cacheFlow(cacheType).onStart {
        updateCurrentCacheFlow(cacheType)
    }

    override suspend fun clearCache(cacheType: CacheType) {
        withContext(Dispatchers.IO) {
            clear(cacheType)
            updateCurrentCacheFlow(cacheType)
        }
    }

    private fun cacheFlow(cacheType: CacheType): MutableSharedFlow<Int> = when(cacheType) {
        LIGHTBOX_PHOTO_DISK -> lightboxPhotoDiskCacheFlow
        LIGHTBOX_PHOTO_MEMORY -> lightboxPhotoMemCacheFlow
        THUMBNAIL_DISK -> thumbnailDiskCacheFlow
        THUMBNAIL_MEMORY -> thumbnailMemCacheFlow
        VIDEO_DISK -> videoDiskCacheFlow
    }

    private fun clear(cacheType: CacheType) = when (cacheType) {
        LIGHTBOX_PHOTO_MEMORY -> lightboxPhotoMemoryCache.clear()
        LIGHTBOX_PHOTO_DISK -> lightboxPhotoDiskCache.clear()
        THUMBNAIL_MEMORY -> fresco.clearMemoryCaches()
        THUMBNAIL_DISK -> fresco.clearDiskCaches()
        VIDEO_DISK -> videoCache.evictAll()
    }

    private fun currentUse(cacheType: CacheType) = when(cacheType) {
        LIGHTBOX_PHOTO_MEMORY -> lightboxPhotoMemoryCache.size.mb
        LIGHTBOX_PHOTO_DISK -> lightboxPhotoDiskCache.size.mb
        THUMBNAIL_MEMORY -> fresco.bitmapMemoryCache.sizeInBytes.mb
        THUMBNAIL_DISK -> fresco.usedDiskCacheSize.mb
        VIDEO_DISK -> (videoCache.cache?.cacheSpace ?: 0).mb
    }

    private fun updateCurrentCacheFlow(cacheType: CacheType) =
        cacheFlow(cacheType).tryEmit(currentUse(cacheType))

    private val Number.mb: Int get() = toInt() / 1024 / 1024
}
