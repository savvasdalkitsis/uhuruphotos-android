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

import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDaySummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.joda.time.format.ISODateTimeFormat
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRepositoryTest {

    private val day = RemoteMediaDaySummaryResponse(id = "20261107", date = "2026-11-07", numberOfItems = 1)
    private val summaries = slot<suspend () -> List<RemoteMediaDaySummaryResponse>>()
    private val clears = mutableListOf<Boolean>()
    private val queries: RemoteMediaCollectionsQueries = mockk()
    private val remoteMediaUseCase: RemoteMediaUseCase = mockk {
        coEvery { getRemoteMediaCollections(any()) } returns listOf(day)
        coEvery {
            processRemoteMediaCollections(capture(summaries), any(), any(), any(), any(), capture(clears))
        } returns Ok(Unit)
    }
    private val underTest = FeedRepository(
        mockk(),
        queries,
        remoteMediaUseCase,
        mockk(relaxed = true) { every { getNullableString("lastFeedRefresh", null) } returns "since" },
        ISODateTimeFormat.dateTime(),
    )

    @Test
    fun `a feed refresh replaces the summaries of the days it refreshes`() = runTest {
        underTest.refreshRemoteFeed()

        assertTrue(clears.single())
    }
}
