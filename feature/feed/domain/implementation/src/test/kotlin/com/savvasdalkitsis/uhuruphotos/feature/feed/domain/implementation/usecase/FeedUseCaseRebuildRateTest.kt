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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel.ALL
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedUseCaseRebuildRateTest {

    private val feedRepository: FeedRepository = mockk<FeedRepository>().defaults()
    private val liveFeeds = mutableListOf<Flow<List<MediaCollectionModel>>>()
    private val underTest = FeedUseCase(
        feedRepository,
        unsharedFeeds(liveFeeds),
        mockk<MediaUseCase>().defaults(),
        mockk(relaxed = true),
        mockk<DownloadUseCase>().defaults(),
        mockk<UploadsUseCase>().defaults(),
        mockk(),
        mockk<WelcomeUseCase>().defaults(),
        mockk<PortfolioUseCase>().defaults(),
    )

    @Test
    fun `rebuilds the feed at most once a second while the server feed keeps changing`() = runTest {
        every { feedRepository.observeRemoteMediaCollectionsByDate(ALL, false) } returns flow {
            repeat(DAYS) { day ->
                emit(Group((0..day).associate { "day$it" to listOf(mediaItem("$it")) }))
                delay(100)
            }
        }

        underTest.observeFeed(ALL, false)
        val builds = liveFeeds.single().toList()

        assertTrue("rebuilt the feed ${builds.size} times", builds.size <= 3)
        assertEquals(DAYS, builds.last().size)
    }

    private companion object {
        const val DAYS = 20
    }
}
