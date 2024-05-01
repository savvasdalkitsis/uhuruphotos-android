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
package com.savvasdalkitsis.uhuruphotos.foundation.video.implementation

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.VideoCacheUseCase

@OptIn(UnstableApi::class)
class VideoCacheUseCase(
    private val videoCache: SimpleCache,
): VideoCacheUseCase {

    override fun clearAll() {
        videoCache.keys.forEach {
            videoCache.removeResource(it)
        }
    }

    override fun cacheSpace(): Int =
        videoCache.cacheSpace.mb

    private val Number.mb: Int get() = toInt() / 1024 / 1024

}