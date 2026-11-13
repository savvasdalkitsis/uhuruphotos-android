/*
Copyright 2026 Savvas Dalkitsis

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

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedCache
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FeedUseCaseLocalMediaTest {

    private val portfolioUseCase: PortfolioUseCase = mockk<PortfolioUseCase>().defaults()
    private val mediaUseCase: MediaUseCase = mockk<MediaUseCase>().defaults()
    private val underTest = FeedUseCase(
        mockk<FeedRepository>().defaults(),
        mockk<FeedCache>(relaxed = true).defaults(),
        mediaUseCase,
        mockk(relaxed = true),
        mockk<DownloadUseCase>().defaults(),
        mockk<UploadsUseCase>().defaults(),
        mockk(),
        mockk<WelcomeUseCase>().defaults(),
        portfolioUseCase,
    )

    @Test
    fun `shows an individually published item once after it moved into a published folder`() = runTest {
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to emptyList(),
            mediaFolders = listOf(localFolder(102) to listOf(localMediaItem(5, "day1"))),
        )
        portfolioUseCase.hasPublishedPortfolio(102)
        every { portfolioUseCase.observeIndividualPortfolioItems() } returns flowOf(listOf(PortfolioItems(5, 101)))

        assertFeed(mediaCollection("local_media_collection_day1", "day1", local(5)))
    }

    private suspend fun assertFeed(vararg collections: MediaCollectionModel) {
        underTest.observeFeed(FeedFetchTypeModel.ALL, false).test {
            assertEquals(collections.toList(), awaitItem())
            awaitComplete()
        }
    }
}
