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

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel.ALL
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedFeedsTest {

    private val feed = listOf(MediaCollectionModel("day", emptyList(), "day"))

    @Test
    fun `lets go of the last feed shortly after nobody observes it`() = runTest {
        val underTest = SharedFeeds(backgroundScope)
        val shared = underTest.share(ALL, false) {
            flow {
                emit(feed)
                awaitCancellation()
            }
        } as SharedFlow<List<MediaCollectionModel>>

        shared.first()
        advanceTimeBy(6_000)
        runCurrent()

        assertEquals(emptyList<List<MediaCollectionModel>>(), shared.replayCache)
    }

    @Test
    fun `builds the feed again after it failed`() = runTest {
        val underTest = SharedFeeds(backgroundScope)
        val broken = underTest.share(ALL, false) { flow { throw IllegalStateException("broken") } }
        backgroundScope.launch { broken.collect() }
        runCurrent()

        val rebuilt = underTest.share(ALL, false) { flow { emit(feed) } }.first()

        assertEquals(feed, rebuilt)
    }
}
