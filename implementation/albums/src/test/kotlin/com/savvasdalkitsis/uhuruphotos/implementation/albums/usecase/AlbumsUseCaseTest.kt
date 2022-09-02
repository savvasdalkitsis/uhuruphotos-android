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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.usecase

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.user.User
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.TestMediaItems.mediaItem
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.TestUsers.user
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestAlbums.album
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestGetAlbums.getPersonAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.reportsHavingAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.reportsHavingNoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.implementation.albums.returnsPersonAlbumWithEntries
import com.savvasdalkitsis.uhuruphotos.implementation.albums.worker.AlbumWorkScheduler
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
import org.junit.Before
import org.junit.Test

class AlbumsUseCaseTest {

    private val albumWorkScheduler = mockk<AlbumWorkScheduler>(relaxed = true)
    private val albumsRepository = mockk<AlbumsRepository>(relaxed = true)
    private val dateDisplayer = mockk<DateDisplayer>(relaxed = true)
    private val remoteMediaUseCase = mockk<RemoteMediaUseCase>(relaxed = true)
    private val userUseCase = mockk<UserUseCase>(relaxed = true)
    private val underTest = AlbumsUseCase(
        albumsRepository,
        dateDisplayer,
        remoteMediaUseCase,
        albumWorkScheduler,
        userUseCase
    )

    @Before
    fun setUp() {
        every { dateDisplayer.dateString(any()) } returns ""
        userUseCase.returnsUser(user)
    }

    @Test
    fun `gets person albums from repository`() = runBlocking {
        albumsRepository.returnsPersonAlbumWithEntries(personId = 1, getPersonAlbum.copy(
            id = "albumId",
            photoId = "photoId",
        ))

        assertThat(underTest.getPersonAlbums(1), sameBeanAs(listOf(album.copy(
            id = "albumId",
            photos = listOf(mediaItem.copy(id = MediaId.Remote("photoId")))
        ))))
    }

    @Test
    fun `observes person albums from repository and updates`() = runBlocking {
        val personAlbums = Channel<Group<String, GetPersonAlbums>> {  }
        coEvery { albumsRepository.observePersonAlbums(1) } returns personAlbums.receiveAsFlow()

        underTest.observePersonAlbums(1).test {
            personAlbums.send(
                Group(mapOf(
                "albumId" to listOf(getPersonAlbum.copy(
                    id = "albumId",
                    photoId = "photoId",
                ))
            ))
            )

            assertThat(awaitItem(), sameBeanAs(listOf(album.copy(
                id = "albumId",
                photos = listOf(mediaItem.copy(id = MediaId.Remote("photoId")))
            ))))
        }
    }

    @Test
    fun `refreshes albums when observing people albums if they are missing from repo`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()

        underTest.observePersonAlbums(1).collect()

        verify { albumWorkScheduler.scheduleAlbumsRefreshNow(false) }
    }

    @Test
    fun `ignores errors when refreshing albums after observing people albums`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()
        every { albumWorkScheduler.scheduleAlbumsRefreshNow(false) } throws IllegalStateException()

        underTest.observePersonAlbums(1).collect()
    }

    @Test
    fun `does not refresh albums when observing people albums if they are not missing from repo`() = runBlocking {
        albumsRepository.reportsHavingAlbums()

        underTest.observePersonAlbums(1).collect()

        verify { albumWorkScheduler wasNot Called}
    }

    private val List<Album>.firstPhoto get() = first().photos.first()
    private val List<Album>.lastPhoto get() = last().photos.last()
    private fun UserUseCase.returnsUser(user: User) {
        coEvery { getUserOrRefresh() } returns Result.success(user)
    }
}
