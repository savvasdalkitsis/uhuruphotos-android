package com.savvasdalkitsis.uhuruphotos.albums.repository

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.albums.TestAlbums.*
import com.savvasdalkitsis.uhuruphotos.albums.TestGetAlbums.getAlbum
import com.savvasdalkitsis.uhuruphotos.albums.TestGetAlbums.getPersonAlbum
import com.savvasdalkitsis.uhuruphotos.albums.TestGetPhotoSummaries.photoSummariesForAlbum
import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumById
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumsByDate
import com.savvasdalkitsis.uhuruphotos.db.TestDatabase
import com.savvasdalkitsis.uhuruphotos.db.albums.Albums
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.savvasdalkitsis.uhuruphotos.photos.TestPhotos.photoSummaryItem
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Test
import com.savvasdalkitsis.uhuruphotos.photos.TestPhotoSummaries.photoSummary as photo

class AlbumsRepositoryTest {

    private val db = TestDatabase.getDb()
    private val albumsService = mockk<AlbumsService>(relaxed = true)
    private val serverAlbumLocation = "serverAlbumLocation"
    private val underTest = AlbumsRepository(
        albumsService, db.albumsQueries, db.personQueries, db.photoSummaryQueries
    )

    @Test
    fun `reports having albums if 1 or more exists`() = runBlocking {
        given(album)

        assert(underTest.hasAlbums())
    }

    @Test
    fun `reports not having albums if none exists`() = runBlocking {
        db.albumsQueries.clearAlbums()

        assert(!underTest.hasAlbums())
    }

    @Test
    fun `gets albums by date`() = runBlocking {
        given(album(1), album(2))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 2),
        )

        underTest.getAlbumsByDate().assertSameAs(
            album(1,
                entry(photo(2)),
                entry(photo(1)),
            ),
            album(2,
                entry(photo(3)),
            ),
        )
    }

    @Test
    fun `observes albums by date and updates`() = runBlocking {
        given(album(1), album(2))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 2),
        )

        underTest.observeAlbumsByDate().test {
            awaitItem().assertSameAs(
                album(1,
                    entry(photo(2)),
                    entry(photo(1)),
                ),
                album(2,
                    entry(photo(3)),
                ),
            )

            insert(photo(4, inAlbum = 2))

            awaitItem().assertSameAs(
                album(1,
                    entry(photo(2)),
                    entry(photo(1)),
                ),
                album(2,
                    entry(photo(4)),
                    entry(photo(3)),
                ),
            )
        }
    }

    @Test
    fun `gets people albums`() = runBlocking {
        given(album(1))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 1),
        )
        given(person = 1).hasPhotos(1, 3)

        underTest.getPersonAlbums(1).assertSameAs(
            album(1,
                entry(personId = 1, photo(3)),
                entry(personId = 1, photo(1)),
            ),
        )
    }

    @Test
    fun `observes people albums and updates`() = runBlocking {
        given(album(1))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 1),
        )
        given(person = 1).hasPhotos(1, 3)

        underTest.observePersonAlbums(1).test {
            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(3)),
                    entry(personId = 1, photo(1)),
                )
            )

            insert(personId = 1, photoId(2))

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(3)),
                    entry(personId = 1, photo(2)),
                    entry(personId = 1, photo(1)),
                )
            )
        }
    }

    @Test
    fun `refreshes people albums when starting to observe`() = runBlocking {
        given(album(1))
        given(photo(1, inAlbum = 1))
        given(person = 1).hasPhotos(1)

        val albumsResponse = Mutex(locked = true)
        coEvery { albumsService.getAlbumsForPerson(1) } coAnswers {
            albumsResponse.withLock {
                AlbumsByDate(
                    2, listOf(
                        incompleteAlbum(1).copy(location = serverAlbumLocation),
                        incompleteAlbum(2).copy(location = serverAlbumLocation),
                    )
                )
            }
        }
        val album1Response = Mutex(locked = true)
        coEvery { albumsService.getAlbumForPerson(albumId(1), 1) } coAnswers {
            album1Response.withLock {
                AlbumById(
                    completeAlbum(1).copy(
                        items = listOf(
                            photoSummaryItem(1),
                            photoSummaryItem(2),
                        )
                    )
                )
            }
        }
        val album2Response = Mutex(locked = true)
        coEvery { albumsService.getAlbumForPerson(albumId(2), 1) } coAnswers {
            album2Response.withLock {
                AlbumById(
                    completeAlbum(2).copy(
                        items = listOf(
                            photoSummaryItem(3),
                            photoSummaryItem(4),
                        )
                    )
                )
            }
        }

        underTest.observePersonAlbums(1).test {
            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(1)),
                )
            )

            albumsResponse.completes()

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(1)).copy(albumLocation = serverAlbumLocation),
                )
            )

            album1Response.completes()

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(1)).withServerResponseData(),
                )
            )

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(2)).withServerResponseData(),
                    entry(personId = 1, photo(1)).withServerResponseData(),
                )
            )

            album2Response.completes()

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(2)).withServerResponseData(),
                    entry(personId = 1, photo(1)).withServerResponseData(),
                ),
                album(2,
                    entry(personId = 1, photo(3)).withServerResponseData(),
                ),
            )

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(2)).withServerResponseData(),
                    entry(personId = 1, photo(1)).withServerResponseData(),
                ),
                album(2,
                    entry(personId = 1, photo(4)).withServerResponseData(),
                    entry(personId = 1, photo(3)).withServerResponseData(),
                ),
            )
        }
    }

    @Test
    fun `refreshes albums on demand`() = runBlocking {
        db.albumsQueries.clearAlbums()
        db.photoSummaryQueries.clearAll()
        coEvery { albumsService.getAlbumsByDate() } returns AlbumsByDate(1, listOf(
            incompleteAlbum(1).copy(location = serverAlbumLocation),
        ))
        coEvery { albumsService.getAlbum(albumId(1)) } returns AlbumById(
            completeAlbum(1).copy(
                items = listOf(
                    photoSummaryItem(1),
                    photoSummaryItem(2),
                )
            )
        )

        underTest.refreshAlbums(shallow = false) {}

        val album1 = album(1)

        assertThat(db.albumsQueries.getAlbums().await(), sameBeanAs(listOf(
            entry(photo(2, inAlbum = 1))
                .copy(id = album1.id, albumDate = album1.date)
                .withServerResponseData(),
            entry(photo(1, inAlbum = 1))
                .copy(id = album1.id, albumDate = album1.date)
                .withServerResponseData(),
        )))

        assertThat(db.photoSummaryQueries.getPhotoSummariesForAlbum(album1.id).await(), sameBeanAs(listOf(
            photoSummariesForAlbum.copy(id = photoId(1)),
            photoSummariesForAlbum.copy(id = photoId(2)),
        )))
    }

    private fun given(vararg albums: Albums) = insert(*albums)

    private fun insert(vararg albums: Albums) = albums.forEach { db.albumsQueries.insert(it) }

    private fun given(vararg photoSummaries: PhotoSummary) = insert(*photoSummaries)

    private fun insert(vararg photoSummaries: PhotoSummary) =
        photoSummaries.forEach { db.photoSummaryQueries.insert(it) }

    @Suppress("SameParameterValue")
    private fun given(person: Int) = PersonContinuation(person)

    private inner class PersonContinuation(val personId: Int) {
        fun hasPhotos(vararg ids: Int) {
            ids.forEach {
                insert(personId, photoId(it))
            }
        }
    }

    private fun insert(personId: Int, photoId: String) {
        db.personQueries.insert(id = null, personId = personId, photoId = photoId )
    }

    private fun <T> Group<String, T>.assertSameAs(vararg pairs: Pair<String, List<T>>) =
        assertThat(this, sameBeanAs(Group(mapOf(*pairs))))

    private fun albumId(id: Int) = "album$id"
    private fun album(id: Int) = album.copy(id = albumId(id), date = "2000-01-0$id")
    private fun photoId(id: Int) = "photo$id"
    private fun photo(id: Int) = photo.copy(id = photoId(id))
    private fun photo(id: Int, inAlbum: Int) = photo.copy(id = photoId(id), containerId = albumId(inAlbum))
    private fun photoSummaryItem(id: Int) = photoSummaryItem.copy(id = photoId(id))
    private fun incompleteAlbum(id: Int) = incompleteAlbum.copy(
        id = album(id).id,
        date = album(id).date
    )
    private fun completeAlbum(id: Int) = completeAlbum.copy(
        id = album(id).id,
        date = album(id).date
    )

    private fun album(album: Int, vararg albums: GetPersonAlbums) = albumId(album) to albums.map {
        it.copy(id = albumId(album), albumDate = album(album).date)
    }
    private fun album(album: Int, vararg albums: GetAlbums) = albumId(album) to albums.map {
        it.copy(id = albumId(album), albumDate = album(album).date)
    }
    private fun entry(photoSummary: PhotoSummary) = getAlbum.copy(photoId = photoSummary.id)
    @Suppress("SameParameterValue")
    private fun entry(personId: Int, photoSummary: PhotoSummary) =
        getPersonAlbum.copy(personId = personId, photoId = photoSummary.id)

    private fun GetPersonAlbums.withServerResponseData() = copy(
        dominantColor = "",
        rating = 0,
        aspectRatio = 1f,
        type = "",
        albumLocation = serverAlbumLocation,
    )
    private fun GetAlbums.withServerResponseData() = copy(
        dominantColor = "",
        rating = 0,
        aspectRatio = 1f,
        type = "",
        albumLocation = serverAlbumLocation,
    )
    private fun Mutex.completes() = unlock()
}
