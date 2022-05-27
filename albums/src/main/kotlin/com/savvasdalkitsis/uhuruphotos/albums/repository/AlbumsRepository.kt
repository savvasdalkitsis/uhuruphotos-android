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
package com.savvasdalkitsis.uhuruphotos.albums.repository

import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.albums.service.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumsByDate
import com.savvasdalkitsis.uhuruphotos.albums.service.model.toAlbum
import com.savvasdalkitsis.uhuruphotos.albums.service.model.toAutoAlbums
import com.savvasdalkitsis.uhuruphotos.db.Database
import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumPeopleQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.db.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.db.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.groupBy
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.infrastructure.model.Group
import com.savvasdalkitsis.uhuruphotos.people.service.model.toPerson
import com.savvasdalkitsis.uhuruphotos.photos.entities.toPhotoSummary
import com.savvasdalkitsis.uhuruphotos.photos.service.model.toPhotoDetails
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AlbumsRepository @Inject constructor(
    private val db: Database,
    private val albumsService: AlbumsService,
    private val albumsQueries: AlbumsQueries,
    private val autoAlbumsQueries: AutoAlbumsQueries,
    private val autoAlbumQueries: AutoAlbumQueries,
    private val autoAlbumPhotosQueries: AutoAlbumPhotosQueries,
    private val autoAlbumPeopleQueries: AutoAlbumPeopleQueries,
    private val personQueries: PersonQueries,
    private val peopleQueries: PeopleQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
    private val photoDetailsQueries: PhotoDetailsQueries,
){

    suspend fun hasAlbums() = albumsQueries.albumsCount().awaitSingle() > 0

    fun observeAlbumsByDate() : Flow<Group<String, GetAlbums>> =
        albumsQueries.getAlbums().asFlow().mapToList().groupBy(GetAlbums::id)
            .distinctUntilChanged()

    suspend fun getAlbumsByDate() : Group<String, GetAlbums> =
        albumsQueries.getAlbums().await().groupBy(GetAlbums::id).let(::Group)

    fun observePersonAlbums(personId: Int) : Flow<Group<String, GetPersonAlbums>> =
        albumsQueries.getPersonAlbums(personId).asFlow().mapToList().groupBy(GetPersonAlbums::id)
            .distinctUntilChanged()
            .safelyOnStartIgnoring {
                downloadPersonAlbums(personId)
            }

    suspend fun getPersonAlbums(personId: Int) : Group<String, GetPersonAlbums> =
        albumsQueries.getPersonAlbums(personId).await().groupBy(GetPersonAlbums::id).let(::Group)

    fun observeAutoAlbums(): Flow<List<AutoAlbums>> =
        autoAlbumsQueries.getAutoAlbums().asFlow().mapToList()
            .distinctUntilChanged()

    fun observeAutoAlbum(albumId: Int): Flow<List<GetAutoAlbum>> =
        autoAlbumQueries.getAutoAlbum(albumId.toString()).asFlow().mapToList()
            .distinctUntilChanged()

    fun observeAutoAlbumPeople(albumId: Int): Flow<List<GetPeopleForAutoAlbum>> =
        autoAlbumPeopleQueries.getPeopleForAutoAlbum(albumId.toString())
            .asFlow().mapToList()

    suspend fun refreshAutoAlbums() {
        val albums = albumsService.getAutoAlbums()
        autoAlbumsQueries.transaction {
            autoAlbumsQueries.clear()
            for (album in albums.results) {
                autoAlbumsQueries.insert(album.toAutoAlbums())
            }
        }
    }

    suspend fun refreshAutoAlbum(albumId: Int) {
        val album = albumsService.getAutoAlbum(albumId.toString())
        db.transaction {
            autoAlbumQueries.insert(
                id = albumId.toString(),
                title = album.title,
                timestamp = album.timestamp,
                createdOn = album.createdOn,
                isFavorite = album.isFavorite,
                gpsLat = album.gpsLat,
                gpsLon = album.gpsLon,
            )
            autoAlbumPeopleQueries.removePeopleForAlbum(albumId.toString())
            for (person in album.people) {
                peopleQueries.insertPerson(person.toPerson())
                autoAlbumPeopleQueries.insert(person.id, albumId.toString())
            }
            autoAlbumPhotosQueries.removePhotosForAlbum(albumId.toString())
            for (photo in album.photos) {
                photoDetailsQueries.insert(photo.toPhotoDetails())
                autoAlbumPhotosQueries.insert(photo.imageHash, albumId.toString())
            }
        }
    }

    private suspend fun downloadPersonAlbums(personId: Int) {
        process(
            albumsFetcher = { albumsService.getAlbumsForPerson(personId) },
            albumFetcher = { albumsService.getAlbumForPerson(it, personId).results },
            shallow = false,
            incompleteAlbumsProcessor = { albums ->
                albumsQueries.transaction {
                    for (album in albums.map { it.toAlbum() }) {
                        albumsQueries.insert(album)
                    }
                }
            },
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
            onProgressChange((100 * ((index + 1)/ albumsToDownloadSummaries.size.toFloat())).toInt())
        }
    }

    private suspend fun maybeFetchSummaries(
        id: String,
        albumFetcher: suspend (String) -> Album.CompleteAlbum,
        completeAlbumProcessor: suspend (Album.CompleteAlbum) -> Unit,
    ) {
        val completeAlbum = albumFetcher(id)
        completeAlbumProcessor(completeAlbum)
        val summaryCount = photoSummaryQueries.getPhotoSummariesCountForAlbum(id).awaitSingle()
        if (completeAlbum.items.size.toLong() != summaryCount) {
            for (albumItem in completeAlbum.items) {
                val photoSummary = albumItem.toPhotoSummary(id)
                photoSummaryQueries.insert(photoSummary)
            }
        }
    }
}