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

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedCache
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FeedUseCaseTest {

    private val portfolioUseCase: PortfolioUseCase = mockk<PortfolioUseCase>().defaults()
    private val welcomeUseCase: WelcomeUseCase = mockk<WelcomeUseCase>().defaults()
    private val preferences: Preferences = mockk()
    private val uploadUseCase: UploadUseCase = mockk<UploadUseCase>().defaults()
    private val downloadUseCase: DownloadUseCase = mockk<DownloadUseCase>().defaults()
    private val feedWorkScheduler: FeedWorkScheduler = mockk()
    private val mediaUseCase: MediaUseCase = mockk<MediaUseCase>().defaults()
    private val feedRepository: FeedRepository = mockk<FeedRepository>().defaults()
    private val feedCache: FeedCache = mockk<FeedCache>(relaxed = true).defaults()
    private val underTest = FeedUseCase(
        feedRepository,
        feedCache,
        mediaUseCase,
        feedWorkScheduler,
        downloadUseCase,
        uploadUseCase,
        preferences,
        welcomeUseCase,
        portfolioUseCase,
    )

    @Test
    fun `loads remote feed`() = runTest {
        feedRepository.returnsRemoteFeed(
            "day1" to listOf(mediaItem("1"), mediaItem("2")),
            "day2" to listOf(mediaItem("3")),
        )

        observeFeed().assert(
            mediaCollection("day1", remote("1"), remote("2")),
            mediaCollection("day2", remote("3")),
        )
    }

    @Test
    fun `loads remote feed marking downloading items as such`() = runTest {
        feedRepository.returnsRemoteFeed(
            "day1" to listOf(mediaItem("1"), mediaItem("2")),
            "day2" to listOf(mediaItem("3")),
        )
        downloadUseCase.isDownloading("2", "3")

        observeFeed().assert(
            mediaCollection("day1", remote("1"), downloading("2")),
            mediaCollection("day2", downloading("3")),
        )
    }

    @Test
    fun `loads local feed from primary folder`() = runTest {
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(
                localMediaItem(1, "day1"),
                localMediaItem(2, "day1"),
                localMediaItem(3, "day2"),
            ),
        )

        observeFeed().assert(
            mediaCollection("day1".localOnlyId, "day1", local(1), local(2)),
            mediaCollection("day2".localOnlyId, "day2", local(3)),
        )
    }

    @Test
    fun `loads local feed marking uploading items as such`() = runTest {
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(
                localMediaItem(1, "day1"),
            ),
        )
        uploadUseCase.hasUploadsInProgress(1)

        observeFeed().assert(
            mediaCollection("day1".localOnlyId, "day1", uploading(1)),
        )
    }

    @Test
    fun `loads local feed from additionally published folders`() = runTest {
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(
                localMediaItem(1, "day1"),
            ),
            mediaFolders = listOf(
                localFolder(101) to listOf(localMediaItem(2, "day1")),
                localFolder(102) to listOf(localMediaItem(3, "day2")),
            ),
        )
        portfolioUseCase.hasPublishedPortfolio(102)

        observeFeed().assert(
            mediaCollection("day1".localOnlyId, "day1", local(1)),
            mediaCollection("day2".localOnlyId, "day2", local(3)),
        )
    }

    @Test
    fun `combines local and remote feed with mutually exclusive dates`() = runTest {
        feedRepository.returnsRemoteFeed(
            "day1" to listOf(mediaItem("1")),
        )
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(
                localMediaItem(2, "day2"),
            ),
        )

        observeFeed().assert(
            mediaCollection("day1", remote("1")),
            mediaCollection("day2".localOnlyId, "day2", local(2)),
        )
    }

    @Test
    fun `combines local and remote feed with common dates`() = runTest {
        feedRepository.returnsRemoteFeed(
            "day1" to listOf(mediaItem("1")),
        )
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(
                localMediaItem(2, "day1"),
            ),
        )

        observeFeed().assert(
            mediaCollection("day1", remote("1"), local(2)),
        )
    }

    @Test
    fun `combines identical local media with remote ones`() = runTest {
        feedRepository.returnsRemoteFeed(
            "day1" to listOf(mediaItem("1", hash = "hash")),
        )
        val local2 = localMediaItem(2, "local day doesn't matter").withHash("hash")
        val local3 = localMediaItem(3, "anything").withHash("hash")
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(local2),
            mediaFolders = listOf(
                localFolder(101) to listOf(local3)
            )
        )
        portfolioUseCase.hasPublishedPortfolio(101)

        observeFeed().assert(
            MediaCollection("day1", listOf(
                mediaGroup(mediaItem(remote("1"), "day1").withHash("hash"), local2, local3)
            ), "day1"),
        )
    }

    @Test
    fun `loads processing feed from primary folder`() = runTest {
        mediaUseCase.returnsLocalMedia(
            primaryFolder = localFolder(100) to listOf(
                localMediaItem(1, "day1"),
                localMediaItem(2, "day1"),
                localMediaItem(3, "day2"),
            ),
        )
        uploadUseCase.hasProcessingInProgress(1, 2, 3)

        observeFeed().assert(
            mediaCollection("day1".localOnlyId, "day1", processing(1), processing(2)),
            mediaCollection("day2".localOnlyId, "day2", processing(3)),
        )
    }

    @Test
    fun `caches feed`() = runTest {
        feedRepository.returnsRemoteFeed(
            "day1" to listOf(mediaItem("1"), mediaItem("2")),
        )

        underTest.observeFeed(FeedFetchType.ALL, false).collect()

        verify { feedCache.cacheFeed(
            listOf(mediaCollection("day1", remote("1"), remote("2"))),
            FeedFetchType.ALL,
            false,
        ) }
    }

    @Test
    fun `loads feed from cache before loading live feed`() = runTest {
        val cached = listOf(
            mediaCollection("day1".localOnlyId, "day1", local(1))
        )
        feedCache.hasFeedCached(cached)
        feedRepository.returnsRemoteFeed(
            "day2" to listOf(mediaItem("2")),
        )

        observeFeed().test {
            expect(cached)
            expect(listOf(mediaCollection("day2", remote("2"))))
            awaitComplete()
        }
    }

    private fun observeFeed() = underTest.observeFeed(FeedFetchType.ALL, false)

    private val String.localOnlyId get() = "local_media_collection_$this"

    private suspend fun Flow<List<MediaCollection>>.assert(vararg collections: MediaCollection) {
        test {
            expect(collections.toList())
            awaitComplete()
        }
    }

    private suspend fun TurbineTestContext<List<MediaCollection>>.expect(collections: List<MediaCollection>) {
        assertThat(awaitItem(), sameBeanAs(collections))
    }

    private fun MediaItemInstance.withHash(hash: String) = copy(mediaHash = MediaItemHash.fromRemoteMediaHash(hash, 0))
}
