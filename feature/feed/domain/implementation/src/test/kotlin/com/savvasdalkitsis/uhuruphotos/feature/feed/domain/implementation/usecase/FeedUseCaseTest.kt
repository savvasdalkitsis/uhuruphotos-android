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
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestGetAlbums.getAlbum
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

    private val albumsRepository = mockk<AlbumsRepository>(relaxed = true)
    private val albumWorkScheduler = mockk<AlbumWorkScheduler>(relaxed = true)
    private val mediaUseCase = mockk<MediaUseCase>(relaxed = true)
    private val preferences = mockk<FlowSharedPreferences>(relaxed = true)
    private val underTest = FeedUseCase(
        albumsRepository,
        mediaUseCase,
        preferences,
    )

    @Test
    fun `observes feed from repository and updates`() = runBlocking {
        val albums = Channel<Group<String, GetAlbums>> {}
        coEvery { albumsRepository.observeAlbumsByDate() } returns albums.receiveAsFlow()

        underTest.observeFeed().test {
            albums.send(
                Group(mapOf(
                    "albumId" to listOf(getAlbum.copy(
                        id = "albumId",
                        photoId = "photoId",
                    ))
                ))
            )
            assertThat(awaitItem(), sameBeanAs(listOf(mediaCollection.copy(
                id = "albumId",
                mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("photoId")))
            ))))
        }
    }

    @Test
    fun `ignores errors when refreshing feed after observing`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()
        every { albumWorkScheduler.scheduleAlbumsRefreshNow(false) } throws IllegalStateException()

        underTest.observeFeed().collect()
    }

    @Test
    fun `refreshes feed when observing if missing from repo`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()

        underTest.observeFeed().collect()

        verify { albumWorkScheduler.scheduleAlbumsRefreshNow(false) }
    }

    @Test
    fun `does not refresh feed when observing if not missing from repo`() = runBlocking {
        albumsRepository.reportsHavingAlbums()

        underTest.observeFeed().collect()

        verify { albumWorkScheduler wasNot Called }
    }

    @Test
    fun `gets feed from repository`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            id = "collectionId",
            photoId = "photoId",
        ))

        assertThat(underTest.getFeed(), sameBeanAs(listOf(
            TestMedia.mediaCollection.copy(
            id = "collectionId",
            mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("photoId")))
        ))))
    }

    private fun AlbumsRepository.reportsHavingNoAlbums() {
        coEvery { hasAlbums() } returns false
    }

    private fun AlbumsRepository.reportsHavingAlbums() {
        coEvery { hasAlbums() } returns true
    }

    private fun AlbumsRepository.returnsAlbumWithEntries(vararg albums: GetAlbums) {
        coEvery { getAlbumsByDate() } returns Group(mapOf("albumId" to albums.toList()))
    }
}