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

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.reportingErrors
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedFeeds internal constructor(private val scope: CoroutineScope) {

    @Inject
    constructor() : this(CoroutineScope(SupervisorJob() + Dispatchers.Default + reportingErrors))

    private val feeds = ConcurrentHashMap<Pair<FeedFetchTypeModel, Boolean>, Flow<List<MediaCollectionModel>>>()

    fun share(
        feedFetchTypeModel: FeedFetchTypeModel,
        loadSmallInitialChunk: Boolean,
        feed: () -> Flow<List<MediaCollectionModel>>,
    ): Flow<List<MediaCollectionModel>> {
        val key = feedFetchTypeModel to loadSmallInitialChunk
        return feeds.getOrPut(key) {
            feed()
                .catch { log(it) }
                .onCompletion { feeds.remove(key) }
                .shareIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS, replayExpirationMillis = 0), replay = 1)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
