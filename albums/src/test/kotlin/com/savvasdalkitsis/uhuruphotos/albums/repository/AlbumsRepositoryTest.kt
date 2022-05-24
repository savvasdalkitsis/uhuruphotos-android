package com.savvasdalkitsis.uhuruphotos.albums.repository

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.albums.ProgressUpdate
import com.savvasdalkitsis.uhuruphotos.albums.TestAlbums.albums
import com.savvasdalkitsis.uhuruphotos.albums.TestGetPhotoSummaries.photoSummariesForAlbum
import com.savvasdalkitsis.uhuruphotos.albums.album
import com.savvasdalkitsis.uhuruphotos.albums.albumId
import com.savvasdalkitsis.uhuruphotos.albums.completeAlbum
import com.savvasdalkitsis.uhuruphotos.albums.completes
import com.savvasdalkitsis.uhuruphotos.albums.entry
import com.savvasdalkitsis.uhuruphotos.albums.incompleteAlbum
import com.savvasdalkitsis.uhuruphotos.albums.photo
import com.savvasdalkitsis.uhuruphotos.albums.photoId
import com.savvasdalkitsis.uhuruphotos.albums.photoSummaryItem
import com.savvasdalkitsis.uhuruphotos.albums.respondsForAlbum
import com.savvasdalkitsis.uhuruphotos.albums.respondsWith
import com.savvasdalkitsis.uhuruphotos.albums.serverAlbumLocation
import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.albums.willRespondForAlbum
import com.savvasdalkitsis.uhuruphotos.albums.willRespondForPersonAlbum
import com.savvasdalkitsis.uhuruphotos.albums.willRespondForPersonWith
import com.savvasdalkitsis.uhuruphotos.albums.willRespondWith
import com.savvasdalkitsis.uhuruphotos.albums.withServerResponseData
import com.savvasdalkitsis.uhuruphotos.db.TestDatabase
import com.savvasdalkitsis.uhuruphotos.db.albums.Albums
import com.savvasdalkitsis.uhuruphotos.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AlbumsRepositoryTest {

    private val db = TestDatabase.getDb()
    private val albumsService = mockk<AlbumsService>(relaxed = true)
    private val underTest = AlbumsRepository(
        albumsService, db.albumsQueries, db.personQueries, db.photoSummaryQueries
    )

    @Test
    fun `reports having albums if 1 or more exists`() = runBlocking {
        given(albums)

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

        val albumsResponse = albumsService.willRespondForPersonWith(personId = 1,
            incompleteAlbum(1).copy(location = serverAlbumLocation),
            incompleteAlbum(2).copy(location = serverAlbumLocation),
        )
        val album1Response = albumsService.willRespondForPersonAlbum(personId = 1, albumId = 1,
            completeAlbum(1).copy(
                items = listOf(
                    photoSummaryItem(1),
                    photoSummaryItem(2),
                )
            )
        )
        val album2Response = albumsService.willRespondForPersonAlbum(personId = 1, albumId = 2,
            completeAlbum(2).copy(
                items = listOf(
                    photoSummaryItem(3),
                    photoSummaryItem(4),
                )
            )
        )

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
        albumsService.respondsWith(
            incompleteAlbum(1).copy(location = serverAlbumLocation),
        )
        albumsService.respondsForAlbum(1,
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

    @Test
    fun `reports refresh progress`() = runBlocking {
        val progress = ProgressUpdate()

        val albumsResponse = albumsService.willRespondWith(
            incompleteAlbum(1),
            incompleteAlbum(2),
        )
        val album1Response = albumsService.willRespondForAlbum(1, completeAlbum(1))
        val album2Response = albumsService.willRespondForAlbum(2, completeAlbum(2))

        CoroutineScope(Dispatchers.Default).launch {
            underTest.refreshAlbums(shallow = false, progress)
        }

        progress.assertReceived(0)
        albumsResponse.completes()

        album1Response.completes()
        progress.assertReceived(50)

        album2Response.completes()
        progress.assertReceived(100)
    }

    @Test
    fun `can perform shallow refresh when asked`() = runBlocking {
        val progress = ProgressUpdate()

        val albumsResponse = albumsService.willRespondWith(
            incompleteAlbum(1),
            incompleteAlbum(2),
            incompleteAlbum(3),
            incompleteAlbum(4),
        )
        val album1Response = albumsService.willRespondForAlbum(1, completeAlbum(1))
        val album2Response = albumsService.willRespondForAlbum(2, completeAlbum(2))
        val album3Response = albumsService.willRespondForAlbum(3, completeAlbum(3))
        albumsService.respondsForAlbum(4, completeAlbum(4))

        CoroutineScope(Dispatchers.Default).launch {
            underTest.refreshAlbums(shallow = true, progress)
        }

        progress.assertReceived(0)
        albumsResponse.completes()

        album1Response.completes()
        progress.assertReceived(33)

        album2Response.completes()
        progress.assertReceived(66)

        album3Response.completes()
        progress.assertReceived(100)

        coVerify(exactly = 0) { albumsService.getAlbum(albumId(4)) }
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
}
