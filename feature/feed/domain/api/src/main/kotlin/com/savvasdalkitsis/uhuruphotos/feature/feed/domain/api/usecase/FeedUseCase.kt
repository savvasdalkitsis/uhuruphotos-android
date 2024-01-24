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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ALL
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow

interface FeedUseCase {

    fun observeFeed(
        feedFetchType: FeedFetchType = ALL,
        loadSmallInitialChunk: Boolean = true,
    ): Flow<List<MediaCollection>>

    fun observeFeedDisplay(): Flow<PredefinedCollageDisplay>
    fun setFeedDisplay(feedDisplay: PredefinedCollageDisplay)
    suspend fun refreshCluster(clusterId: String): SimpleResult
    fun refreshFeed(shallow: Boolean)
    suspend fun hasFeed(): Boolean
}