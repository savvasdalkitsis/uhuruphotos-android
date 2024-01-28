package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
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
import kotlinx.coroutines.flow.Flow
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
    private val underTest = FeedUseCase(
        feedRepository,
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
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

        underTest.observeFeed().assert(
            MediaCollection("day1", listOf(
                mediaGroup(mediaItem(remote("1"), "day1").withHash("hash"), local2, local3)
            ), "day1"),
        )
    }

    private val String.localOnlyId get() = "local_media_collection_$this"

    private suspend fun Flow<List<MediaCollection>>.assert(vararg collections: MediaCollection) {
        test {
            assertThat(awaitItem(), sameBeanAs(collections.toList()))
            awaitComplete()
        }
    }

    private fun MediaItemInstance.withHash(hash: String) = copy(mediaHash = MediaItemHash(hash))
}
