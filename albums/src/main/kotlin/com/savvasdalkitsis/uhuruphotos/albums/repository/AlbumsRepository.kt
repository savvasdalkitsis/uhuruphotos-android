package com.savvasdalkitsis.uhuruphotos.albums.repository

import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.albums.service.model.toAlbum
import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.groupBy
import com.savvasdalkitsis.uhuruphotos.photos.entities.toPhotoSummary
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AlbumsRepository @Inject constructor(
    private val albumsService: AlbumsService,
    private val albumsQueries: AlbumsQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
){

    suspend fun hasAlbums() = albumsQueries.albumsCount().awaitSingle() > 0

    fun getAlbumsByDate() : Flow<Group<String, GetAlbums>> =
        albumsQueries.getAlbums().asFlow().mapToList().groupBy(GetAlbums::id)
            .distinctUntilChanged()

    suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit) {
        onProgressChange(0)
        val albums = albumsService.getAlbumsByDate()

        albumsQueries.transaction {
            albumsQueries.clearAlbums()
            for (album in albums.results.map { it.toAlbum() }) {
                albumsQueries.insert(album)
            }
        }

        val albumsToDownloadSummaries = when {
            shallow -> albums.results.take(3)
            else -> albums.results
        }
        for ((index, incompleteAlbum) in albumsToDownloadSummaries.withIndex()) {
            val id = incompleteAlbum.id
            maybeFetchSummaries(id)
            onProgressChange((100 * (index / albumsToDownloadSummaries.size.toFloat())).toInt())
        }
    }

    private suspend fun maybeFetchSummaries(id: String) {
        val completeAlbum = albumsService.getAlbum(id).results
        val summaryCount = photoSummaryQueries.getPhotoSummariesCountForAlbum(id).awaitSingle()
        if (completeAlbum.items.size.toLong() != summaryCount) {
            for (albumItem in completeAlbum.items) {
                val photoSummary = albumItem.toPhotoSummary(id)
                photoSummaryQueries.insert(photoSummary)
            }
        }
    }
}