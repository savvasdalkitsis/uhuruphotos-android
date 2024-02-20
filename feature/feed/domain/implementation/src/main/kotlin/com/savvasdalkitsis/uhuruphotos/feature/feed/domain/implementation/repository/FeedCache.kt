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
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ALL
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ONLY_WITH_DATES
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.awaitOnIO
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Singleton
class FeedCache @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    private val cacheFiles: Map<Pair<FeedFetchType, Boolean>, File?> = mapOf(
        (ALL to true) to File(context.cacheDir, "feed_all_small_chunk"),
        (ALL to false) to File(context.cacheDir, "feed_all"),
        (ONLY_WITH_DATES to true) to File(context.cacheDir, "feed_only_with_dates_small_chunk"),
        (ONLY_WITH_DATES to false) to File(context.cacheDir, "feed_only_with_dates"),
    )
    private val caches: Map<Pair<FeedFetchType, Boolean>, MutableSharedFlow<ByteArray>?> = mapOf(
        (ALL to true) to MutableSharedFlow(),
        (ALL to false) to MutableSharedFlow(),
        (ONLY_WITH_DATES to true) to MutableSharedFlow(),
        (ONLY_WITH_DATES to false) to MutableSharedFlow(),
    )

    private fun cache(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ): MutableSharedFlow<ByteArray> = caches[feedFetchType to loadSmallInitialChunk] ?: MutableSharedFlow()

    private fun cacheFile(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ): File? = cacheFiles[feedFetchType to loadSmallInitialChunk]

    fun observeFeed(
        feedFetchType: FeedFetchType = ALL,
        loadSmallInitialChunk: Boolean = true,
    ): Flow<List<MediaCollection>> = cache(feedFetchType, loadSmallInitialChunk).mapNotNull {
        awaitOnIO {
            try {
                ProtoBuf.decodeFromByteArray<List<MediaCollection>>(it)
            } catch (e: Exception) {
                log(e) { "Error decoding feed from proto" }
                null
            }
        }
    }.onStart {
        onIO {
            try {
                cacheFile(feedFetchType, loadSmallInitialChunk)?.let {
                    val value = it.readBytes()
                    if (value.isNotEmpty()) {
                        cache(feedFetchType, loadSmallInitialChunk).tryEmit(value)
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun cacheFeed(
        feed: List<MediaCollection>,
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ) = onIO {
        val bytes = ProtoBuf.encodeToByteArray(feed)
        cache(feedFetchType, loadSmallInitialChunk).emit(bytes)
        cacheFile(feedFetchType, loadSmallInitialChunk)?.writeBytes(bytes)
    }
}