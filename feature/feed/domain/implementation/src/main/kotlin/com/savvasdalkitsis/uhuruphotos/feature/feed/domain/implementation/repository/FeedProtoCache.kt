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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEmpty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class FeedProtoCache @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    private val cacheFile = File(context.cacheDir, "feed.proto")
    private val cache: MutableSharedFlow<ByteArray> = MutableSharedFlow()

    fun observeFeed(): Flow<List<MediaCollection>> = cache.mapNotNull {
        try {
            ProtoBuf.decodeFromByteArray<List<MediaCollection>>(it)
        } catch (e: Exception) {
            log(e) { "Error decoding feed from proto" }
            null
        }
    }.onEmpty {
        val value = cacheFile.readBytes()
        if (value.isNotEmpty()) {
            cache.tryEmit(value)
        }
    }

    fun cacheFeed(feed: List<MediaCollection>) = onIO {
        val bytes = ProtoBuf.encodeToByteArray(feed)
        cache.emit(bytes)
        cacheFile.writeBytes(bytes)
    }
}