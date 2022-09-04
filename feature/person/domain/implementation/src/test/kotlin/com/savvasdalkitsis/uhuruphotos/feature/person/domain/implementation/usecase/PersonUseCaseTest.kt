package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.usecase

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestGetAlbums.getPersonAlbum
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

class PersonUseCaseTest {

    private val albumsRepository = mockk<AlbumsRepository>(relaxed = true)
    private val mediaUseCase = mockk<MediaUseCase>(relaxed = true)
    private val underTest = PersonUseCase(
        albumsRepository,
        mediaUseCase,
    )

    @Test
    fun `observes person albums from repository and updates`() = runBlocking {
        val personAlbums = Channel<Group<String, GetPersonAlbums>> {  }
        coEvery { albumsRepository.observePersonAlbums(1) } returns personAlbums.receiveAsFlow()

        underTest.observePersonMedia(1).test {
            personAlbums.send(
                Group(mapOf(
                    "collectionId" to listOf(getPersonAlbum.copy(
                        id = "collectionId",
                        photoId = "mediaItemId",
                    ))
                ))
            )

            assertThat(awaitItem(), sameBeanAs(listOf(mediaCollection.copy(
                id = "collectionId",
                mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("mediaItemId")))
            ))))
        }
    }

    @Test
    fun `refreshes albums when observing people albums if they are missing from repo`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()

        underTest.observePersonMedia(1).collect()

        verify { mediaUseCase.refreshMediaSummaries(false) }
    }

    @Test
    fun `ignores errors when refreshing albums after observing people albums`() = runBlocking {
        albumsRepository.reportsHavingNoAlbums()
        every { mediaUseCase.refreshMediaSummaries(false) } throws IllegalStateException()

        underTest.observePersonMedia(1).collect()
    }

    @Test
    fun `does not refresh albums when observing people albums if they are not missing from repo`() = runBlocking {
        albumsRepository.reportsHavingAlbums()

        underTest.observePersonMedia(1).collect()

        verify { mediaUseCase wasNot Called }
    }


    @Test
    fun `gets person albums from repository`() = runBlocking {
        albumsRepository.returnsPersonAlbumWithEntries(personId = 1, getPersonAlbum.copy(
            id = "collectionId",
            photoId = "mediaItemId",
        ))

        assertThat(underTest.getPersonMedia(1), sameBeanAs(listOf(mediaCollection.copy(
            id = "collectionId",
            mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("mediaItemId")))
        ))))
    }

    private fun AlbumsRepository.reportsHavingNoAlbums() {
        coEvery { hasAlbums() } returns false
    }

    private fun AlbumsRepository.reportsHavingAlbums() {
        coEvery { hasAlbums() } returns true
    }

    private fun AlbumsRepository.returnsPersonAlbumWithEntries(personId: Int, vararg albums: GetPersonAlbums) {
        coEvery { getPersonAlbums(personId) } returns Group(mapOf("albumId" to albums.toList()))
    }
}