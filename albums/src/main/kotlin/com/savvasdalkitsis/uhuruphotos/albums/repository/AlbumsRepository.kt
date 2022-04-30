package com.savvasdalkitsis.uhuruphotos.albums.repository

import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.albums.service.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumsByDate
import com.savvasdalkitsis.uhuruphotos.albums.service.model.toAlbum
import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.db.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.groupBy
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.photos.entities.toPhotoSummary
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AlbumsRepository @Inject constructor(
    private val albumsService: AlbumsService,
    private val albumsQueries: AlbumsQueries,
    private val personQueries: PersonQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
){

    suspend fun hasAlbums() = albumsQueries.albumsCount().awaitSingle() > 0

    fun getAlbumsByDate() : Flow<Group<String, GetAlbums>> =
        albumsQueries.getAlbums().asFlow().mapToList().groupBy(GetAlbums::id)
            .distinctUntilChanged()

    fun getPersonAlbums(personId: Int) : Flow<Group<String, GetPersonAlbums>> =
        albumsQueries.getPersonAlbums(personId).asFlow().mapToList().groupBy(GetPersonAlbums::id)
            .distinctUntilChanged()
            .safelyOnStartIgnoring {
                downloadPersonAlbums(personId)
            }

    private suspend fun downloadPersonAlbums(personId: Int) {
        process(
            albumsFetcher = { albumsService.getAlbumsForPerson(personId) },
            albumFetcher = { albumsService.getAlbumForPerson(it, personId).results },
            shallow = false,
            completeAlbumProcessor = { album ->
                for (photo in album.items) {
                    personQueries.insert(
                        id = null,
                        personId = personId,
                        photoId = photo.id
                    )
                }
            }
        )
    }

    suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit) {
        process(
            albumsFetcher = { albumsService.getAlbumsByDate() },
            albumFetcher = { albumsService.getAlbum(it).results },
            shallow = shallow,
            onProgressChange = onProgressChange,
            incompleteAlbumsProcessor = { albums ->
                albumsQueries.transaction {
                    albumsQueries.clearAlbums()
                    for (album in albums.map { it.toAlbum() }) {
                        albumsQueries.insert(album)
                    }
                }
            }
        )
    }

    private suspend fun process(
        albumsFetcher: suspend () -> AlbumsByDate,
        albumFetcher: suspend (String) -> Album.CompleteAlbum,
        shallow: Boolean,
        onProgressChange: suspend (Int) -> Unit = {},
        incompleteAlbumsProcessor: suspend (List<Album.IncompleteAlbum>) -> Unit = {},
        completeAlbumProcessor: suspend (Album.CompleteAlbum) -> Unit = {},
    ) {
        onProgressChange(0)
        val albums = albumsFetcher()
        incompleteAlbumsProcessor(albums.results)
        val albumsToDownloadSummaries = when {
            shallow -> albums.results.take(3)
            else -> albums.results
        }
        for ((index, incompleteAlbum) in albumsToDownloadSummaries.withIndex()) {
            val id = incompleteAlbum.id
            maybeFetchSummaries(id, albumFetcher, completeAlbumProcessor)
            onProgressChange((100 * (index / albumsToDownloadSummaries.size.toFloat())).toInt())
        }
    }

    private suspend fun maybeFetchSummaries(
        id: String,
        albumFetcher: suspend (String) -> Album.CompleteAlbum,
        completeAlbumProcessor: suspend (Album.CompleteAlbum) -> Unit,
    ) {
        val completeAlbum = albumFetcher(id)
        val summaryCount = photoSummaryQueries.getPhotoSummariesCountForAlbum(id).awaitSingle()
        completeAlbumProcessor(completeAlbum)
        if (completeAlbum.items.size.toLong() != summaryCount) {
            for (albumItem in completeAlbum.items) {
                val photoSummary = albumItem.toPhotoSummary(id)
                photoSummaryQueries.insert(photoSummary)
            }
        }
    }
}