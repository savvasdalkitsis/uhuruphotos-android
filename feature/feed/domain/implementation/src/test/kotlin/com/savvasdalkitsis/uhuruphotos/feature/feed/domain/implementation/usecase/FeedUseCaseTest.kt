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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestRemoteMediaCollections.getRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalktsis.uhuruphotos.foundation.download.api.usecase.DownloadUseCase
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FeedUseCaseTest {

    private val feedRepository = mockk<FeedRepository>(relaxed = true)
    private val feedWorkScheduler = mockk<FeedWorkScheduler>(relaxed = true)
    private val mediaUseCase = mockk<MediaUseCase>(relaxed = true)
    private val downloadUseCase = mockk<DownloadUseCase>(relaxed = true)
    private val preferences = mockk<Preferences>(relaxed = true)
    private val underTest = FeedUseCase(
        feedRepository,
        mediaUseCase,
        feedWorkScheduler,
        downloadUseCase,
        preferences,
    )

    @Test
    fun `observes feed from repository and updates`() = runBlocking {
        val albums = Channel<Group<String, GetRemoteMediaCollections>> {}
        coEvery { feedRepository.observeRemoteMediaCollectionsByDate() } returns albums.receiveAsFlow()

        underTest.observeFeed().test {
            albums.send(
                Group(mapOf(
                    "albumId" to listOf(getRemoteMediaCollections.copy(
                        id = "albumId",
                        photoId = "photoId",
                    ))
                ))
            )
            assertThat(awaitItem(), sameBeanAs(listOf(mediaCollection.copy(
                id = "albumId",
                mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("photoId", false, "serverUrl")))
            ))))
        }
    }

    @Test
    fun `ignores errors when refreshing feed after observing`() = runBlocking {
        feedRepository.reportsHavingNoAlbums()
        every { feedWorkScheduler.scheduleFeedRefreshNow(false) } throws IllegalStateException()

        underTest.observeFeed().collect()
    }

    @Test
    fun `refreshes feed when observing if missing from repo`() = runBlocking {
        feedRepository.reportsHavingNoAlbums()

        underTest.observeFeed().collect()

        verify { feedWorkScheduler.scheduleFeedRefreshNow(false) }
    }

    @Test
    fun `does not refresh feed when observing if not missing from repo`() = runBlocking {
        feedRepository.reportsHavingAlbums()

        underTest.observeFeed().collect()

        verify { feedWorkScheduler wasNot Called }
    }

    @Test
    fun `gets feed from repository`() = runBlocking {
        feedRepository.returnsAlbumWithEntries(getRemoteMediaCollections.copy(
            id = "collectionId",
            photoId = "photoId",
        ))

        assertThat(underTest.getFeed(), sameBeanAs(listOf(
            mediaCollection.copy(
            id = "collectionId",
            mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("photoId", false, "serverUrl")))
        ))))
    }

    private fun FeedRepository.reportsHavingNoAlbums() {
        coEvery { hasRemoteMediaCollections() } returns false
    }

    private fun FeedRepository.reportsHavingAlbums() {
        coEvery { hasRemoteMediaCollections() } returns true
    }

    private fun FeedRepository.returnsAlbumWithEntries(vararg albums: GetRemoteMediaCollections) {
        coEvery { getRemoteMediaCollectionsByDate() } returns Group(mapOf("albumId" to albums.toList()))
    }
}