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
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.user.User
import com.savvasdalkitsis.uhuruphotos.api.group.model.Group
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestAlbums.album
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestGetAlbums.getAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestGetAlbums.getPersonAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.reportsHavingAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.reportsHavingNoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.implementation.albums.returnsAlbumWithEntries
import com.savvasdalkitsis.uhuruphotos.implementation.albums.returnsAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.returnsPersonAlbumWithEntries
import com.savvasdalkitsis.uhuruphotos.implementation.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.implementation.media.TestMediaItems.mediaItem
import com.savvasdalkitsis.uhuruphotos.implementation.user.TestUsers.user
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
    fun `starts shallow refresh album work`() {
        underTest.startRefreshAlbumsWork(true)

        verify { albumWorkScheduler.scheduleAlbumsRefreshNow(true) }
    }

    @Test
    fun `starts non shallow refresh album work`() {
        underTest.startRefreshAlbumsWork(false)

        verify { albumWorkScheduler.scheduleAlbumsRefreshNow(false) }
    }

    @Test
    fun `gets person albums from repository`() = runBlocking {
        albumsRepository.returnsPersonAlbumWithEntries(personId = 1, getPersonAlbum.copy(
            id = "albumId",
            photoId = "photoId",
        ))

        assertThat(underTest.getPersonAlbums(1), sameBeanAs(listOf(album.copy(
            id = "albumId",
            photos = listOf(mediaItem.copy(id = "photoId"))
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
                photos = listOf(mediaItem.copy(id = "photoId"))
            ))))
        }
    }

    @Test
    fun `gets albums from repository`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            id = "albumId",
            photoId = "photoId",
        ))

        assertThat(underTest.getAlbums(), sameBeanAs(listOf(album.copy(
            id = "albumId",
            photos = listOf(mediaItem.copy(id = "photoId"))
        ))))
    }

    @Test
    fun `maps date from repository`() = runBlocking {
        every { dateDisplayer.dateString("date") } returns "formatted"
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            albumDate = "date",
        ))

        assert(underTest.getAlbums().last().displayTitle == "formatted")
    }

    @Test
    fun `maps thumbnail url for photos`() = runBlocking {
        every { with(remoteMediaUseCase) { "photoId".toThumbnailUrlFromId() } } returns "thumbnail"
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            photoId = "photoId",
        ))

        assert(underTest.getAlbums().lastPhoto.thumbnailUri == "thumbnail")
    }

    @Test
    fun `maps full url for photos and videos`() = runBlocking {
        every { with(remoteMediaUseCase) { "photoId".toFullSizeUrlFromId(false) } } returns "photoUrl"
        every { with(remoteMediaUseCase) { "photoId".toFullSizeUrlFromId(true) } } returns "videoUrl"
        albumsRepository.returnsAlbumWithEntries(
            getAlbum.copy(
                type = "photo",
            ),
            getAlbum.copy(
                type = "video",
            ),
        )

        assert(underTest.getAlbums().firstPhoto.fullResUri == "photoUrl")
        assert(underTest.getAlbums().lastPhoto.fullResUri == "videoUrl")
    }

    @Test
    fun `maps fallback color for photos`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            dominantColor = "color",
        ))

        assert(underTest.getAlbums().lastPhoto.fallbackColor == "color")
    }

    @Test
    fun `maps photo as favourite is rating is at threshold`() = runBlocking {
        userUseCase.returnsUser(user.copy(favoriteMinRating = 999))

        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            rating = 999
        ))

        assert(underTest.getAlbums().lastPhoto.isFavourite)
    }

    @Test
    fun `maps photo as favourite is rating is over threshold`() = runBlocking {
        userUseCase.returnsUser(user.copy(favoriteMinRating = 999))
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            rating = 1000
        ))

        assert(underTest.getAlbums().lastPhoto.isFavourite)
    }

    @Test
    fun `maps photo as not favourite is rating is below threshold`() = runBlocking {
        userUseCase.returnsUser(user.copy(favoriteMinRating = 999))
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            rating = 998
        ))

        assert(!underTest.getAlbums().lastPhoto.isFavourite)
    }

    @Test
    fun `maps photo as not favourite is rating is missing`() = runBlocking {
        userUseCase.returnsUser(user.copy(favoriteMinRating = 999))
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            rating = null
        ))

        assert(!underTest.getAlbums().lastPhoto.isFavourite)
    }


    @Test
    fun `maps photo as not favourite is user favourite threshold is missing`() = runBlocking {
        userUseCase.returnsUser(user.copy(favoriteMinRating = null))
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            rating = 999
        ))

        assert(!underTest.getAlbums().lastPhoto.isFavourite)
    }

    @Test
    fun `maps photo aspect ratio`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            aspectRatio = 1.2f
        ))

        assert(underTest.getAlbums().lastPhoto.ratio == 1.2f)
    }

    @Test
    fun `maps photo aspect ratio to square if missing`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            aspectRatio = null
        ))

        assert(underTest.getAlbums().lastPhoto.ratio == 1f)
    }

    @Test
    fun `maps item as video`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            type = "video"
        ))

        assert(underTest.getAlbums().lastPhoto.isVideo)
    }

    @Test
    fun `maps item as not video`() = runBlocking {
        albumsRepository.returnsAlbumWithEntries(getAlbum.copy(
            type = "anything_but_video"
        ))

        assert(!underTest.getAlbums().lastPhoto.isVideo)
    }

    @Test
    fun `filters items without id`() = runBlocking {
        albumsRepository.returnsAlbums(
            "album1" to listOf(
                getAlbum,
                getAlbum.copy(
                    photoId = null,
                ),
            ),
            "album2" to listOf(
                getAlbum.copy(
                    photoId = " ",
                ),
                getAlbum,
            )
        )

        assert(underTest.getAlbums().all { it.photos.size == 1 })
    }

    @Test
    fun `observes albums from repository and updates`() = runBlocking {
        val albums = Channel<Group<String, GetAlbums>> {}
        coEvery { albumsRepository.observeAlbumsByDate() } returns albums.receiveAsFlow()

        underTest.observeAlbums().test {
            albums.send(
                Group(mapOf(
                "albumId" to listOf(getAlbum.copy(
                    id = "albumId",
                    photoId = "photoId",
                ))
            ))
            )
            assertThat(awaitItem(), sameBeanAs(listOf(album.copy(
                id = "albumId",
                photos = listOf(mediaItem.copy(id = "photoId"))
            ))))
        }
    }

    @Test
    fun `refreshes albums when observing if they are missing from repo`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()

        underTest.observeAlbums().collect()

        verify { albumWorkScheduler.scheduleAlbumsRefreshNow(false) }
    }

    @Test
    fun `refreshes albums when observing people albums if they are missing from repo`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()

        underTest.observePersonAlbums(1).collect()

        verify { albumWorkScheduler.scheduleAlbumsRefreshNow(false) }
    }


    @Test
    fun `ignores errors when refreshing albums after observing`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()
        every { albumWorkScheduler.scheduleAlbumsRefreshNow(false) } throws IllegalStateException()

        underTest.observeAlbums().collect()
    }

    @Test
    fun `ignores errors when refreshing albums after observing people albums`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()
        every { albumWorkScheduler.scheduleAlbumsRefreshNow(false) } throws IllegalStateException()

        underTest.observePersonAlbums(1).collect()
    }

    @Test
    fun `does not refresh albums when observing if they are not missing from repo`() = runBlocking {
        albumsRepository.reportsHavingAlbums()

        underTest.observeAlbums().collect()

        verify { albumWorkScheduler wasNot Called}
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
