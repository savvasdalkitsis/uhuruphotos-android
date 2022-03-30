package com.savvasdalkitsis.librephotos.albums.repository

import com.savvasdalkitsis.librephotos.albums.api.AlbumsService
import com.savvasdalkitsis.librephotos.albums.api.model.toAlbum
import com.savvasdalkitsis.librephotos.albums.db.AlbumsQueries
import com.savvasdalkitsis.librephotos.albums.db.GetAlbums
import com.savvasdalkitsis.librephotos.extensions.Group
import com.savvasdalkitsis.librephotos.extensions.awaitSingle
import com.savvasdalkitsis.librephotos.extensions.groupBy
import com.savvasdalkitsis.librephotos.photos.db.PhotoSummaryQueries
import com.savvasdalkitsis.librephotos.photos.db.entities.toPhotoSummary
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
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

    suspend fun refreshAlbums() {
        val albums = albumsService.getAlbumsByDate()

        albumsQueries.transaction {
            albumsQueries.clearAlbums()
            for (album in albums.results.map { it.toAlbum() }) {
                albumsQueries.insert(album)
            }
        }

        for (incompleteAlbum in albums.results) {
            val id = incompleteAlbum.id
            maybeFetchSummaries(id)
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