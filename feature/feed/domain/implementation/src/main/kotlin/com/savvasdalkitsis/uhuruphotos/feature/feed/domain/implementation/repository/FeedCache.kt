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

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ALL
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ONLY_WITH_DATES
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedCache @Inject constructor() {

    private val caches: Map<Pair<FeedFetchType, Boolean>, MutableSharedFlow<List<MediaCollection>>?> = mapOf(
        (ALL to true) to MutableSharedFlow(replay = 1),
        (ALL to false) to MutableSharedFlow(replay = 1),
        (ONLY_WITH_DATES to true) to MutableSharedFlow(replay = 1),
        (ONLY_WITH_DATES to false) to MutableSharedFlow(replay = 1),
    )

    private fun cache(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ): MutableSharedFlow<List<MediaCollection>> =
        caches[feedFetchType to loadSmallInitialChunk] ?: MutableSharedFlow()

    fun observeFeed(
        feedFetchType: FeedFetchType = ALL,
        loadSmallInitialChunk: Boolean = true,
    ): Flow<List<MediaCollection>> = cache(feedFetchType, loadSmallInitialChunk)

    fun cacheFeed(
        feed: List<MediaCollection>,
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ) = onIO {
        cache(feedFetchType, loadSmallInitialChunk).emit(feed)
    }
}