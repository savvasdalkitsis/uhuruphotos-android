/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.cache

import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.minCacheSize
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.implementation.cache.ImageCacheUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object ImageCacheModule {

    val fullImageMemoryCache: MemoryCache by singleInstance {
        MemoryCache.Builder(AndroidModule.applicationContext)
            .maxSizeBytes(SettingsModule.settingsUseCase.getLightboxPhotoMemCacheMaxLimit().mb.positiveInt)
            .build()
    }

    val thumbnailImageMemoryCache: MemoryCache by singleInstance {
        MemoryCache.Builder(AndroidModule.applicationContext)
            .maxSizeBytes(SettingsModule.settingsUseCase.getThumbnailMemCacheMaxLimit().mb.positiveInt)
            .build()
    }

    val fullImageDiskCache: DiskCache by singleInstance {
        DiskCache.Builder()
            .directory(AndroidModule.applicationContext.cacheDir.resolve("image_cache_full"))
            .maxSizeBytes(SettingsModule.settingsUseCase.getLightboxPhotoDiskCacheMaxLimit().mb)
            .build()
    }

    val thumbnailImageDiskCache: DiskCache by singleInstance {
        DiskCache.Builder()
            .directory(AndroidModule.applicationContext.cacheDir.resolve("image_cache")) // keeping same name for backwards compatibility
            .maxSizeBytes(SettingsModule.settingsUseCase.getThumbnailDiskCacheMaxLimit().mb)
            .build()
    }

    val imageCacheUseCase: com.savvasdalkitsis.uhuruphotos.foundation.image.api.cache.ImageCacheUseCase
        get() = ImageCacheUseCase(
            fullMemoryCache = fullImageMemoryCache,
            fullDiskCache = fullImageDiskCache,
            thumbnailMemoryCache = thumbnailImageMemoryCache,
            thumbnailDiskCache = thumbnailImageDiskCache,
        )

    private val Int.mb get() = (coerceAtLeast(minCacheSize) * 1024 * 1024L)
    private val Long.positiveInt get() = coerceIn(minCacheSize.toLong()..Int.MAX_VALUE).toInt()


}