package com.savvasdalkitsis.librephotos.albums.repository

import com.savvasdalkitsis.librephotos.albums.api.AlbumsService
import com.savvasdalkitsis.librephotos.albums.api.model.toAlbum
import com.savvasdalkitsis.librephotos.albums.db.AlbumsQueries
import com.savvasdalkitsis.librephotos.albums.db.GetAlbums
import com.savvasdalkitsis.librephotos.extensions.Group
import com.savvasdalkitsis.librephotos.extensions.await
import com.savvasdalkitsis.librephotos.extensions.awaitSingle
import com.savvasdalkitsis.librephotos.extensions.groupBy
import com.savvasdalkitsis.librephotos.photos.api.PhotosService
import com.savvasdalkitsis.librephotos.photos.db.GetPhotoSummariesForAlbum
import com.savvasdalkitsis.librephotos.photos.db.PhotoDetailsQueries
import com.savvasdalkitsis.librephotos.photos.db.PhotoSummaryQueries
import com.savvasdalkitsis.librephotos.photos.db.entities.toPhotoDetails
import com.savvasdalkitsis.librephotos.photos.db.entities.toPhotoSummary
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumsRepository @Inject constructor(
    private val albumsService: AlbumsService,
    private val photosService: PhotosService,
    private val albumsQueries: AlbumsQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
    private val photoDetailsQueries: PhotoDetailsQueries,
){

    fun getAlbumsByDate() : Flow<Group<String, GetAlbums>> =
        albumsQueries.getAlbums().asFlow().mapToList().groupBy(GetAlbums::id)

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

            for (summary in photoSummaryQueries.getPhotoSummariesForAlbum(id).await()) {
                summary.maybeFetchDetails(id)
            }
        }
    }

    private suspend fun GetPhotoSummariesForAlbum.maybeFetchDetails(id: String) {
        val photoResult = photosService.getPhoto(url)
        photoDetailsQueries.insert(photoResult.toPhotoDetails(id))
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