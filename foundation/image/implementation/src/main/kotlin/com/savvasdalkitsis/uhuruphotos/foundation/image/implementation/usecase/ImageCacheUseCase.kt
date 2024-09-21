/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.image.implementation.usecase

import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.FullImage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.Location
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.Location.DISK
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.Location.MEMORY
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.ThumbnailImage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.Type
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.Type.FULL
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.Type.THUMB
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.usecase.ImageCacheUseCase
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class ImageCacheUseCase @Inject constructor(
    @FullImage
    private val fullMemoryCache: MemoryCache,
    @FullImage
    private val fullDiskCache: DiskCache,
    @ThumbnailImage
    private val thumbnailMemoryCache: MemoryCache,
    @ThumbnailImage
    private val thumbnailDiskCache: DiskCache,
) : ImageCacheUseCase {

    override fun clear(location: Location, type: Type) {
        when(location) {
            MEMORY -> when(type) {
                FULL -> fullMemoryCache.clear()
                THUMB -> thumbnailMemoryCache.clear()
            }
            DISK -> when(type) {
                FULL -> fullDiskCache.clear()
                THUMB -> thumbnailDiskCache.clear()
            }
        }
    }

    override fun getCurrentUse(location: Location, type: Type): Int = when(location) {
        MEMORY -> when(type) {
            FULL -> fullMemoryCache.size
            THUMB -> thumbnailMemoryCache.size
        }
        DISK -> when(type) {
            FULL -> fullDiskCache.size
            THUMB -> thumbnailDiskCache.size
        }
    }.mb

    override fun clearAll() {
        for (location in Location.entries) {
            for (type in Type.entries) {
                clear(location, type)
            }
        }
    }

    private val Number.mb: Int get() = toInt() / 1024 / 1024

}