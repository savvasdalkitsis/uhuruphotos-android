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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.repository

import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AlbumsByDate
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.toAlbum
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.toAutoAlbums
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.toUserAlbums
import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.db.Database
import com.savvasdalkitsis.uhuruphotos.api.db.albums.*
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.api.db.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.api.db.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.api.group.model.Group
import com.savvasdalkitsis.uhuruphotos.api.group.model.groupBy
import com.savvasdalkitsis.uhuruphotos.api.people.service.model.toPerson
import com.savvasdalkitsis.uhuruphotos.api.photos.entities.toPhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.photos.model.toPhotoDetails
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class AlbumsRepository @Inject constructor(
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
    private val userAlbumsQueries: UserAlbumsQueries,
    private val userAlbumQueries: UserAlbumQueries,
    private val userAlbumPhotosQueries: UserAlbumPhotosQueries,
) : AlbumsRepository {

    private var allAlbums: Group<String, GetAlbums> = Group(emptyMap())

    override suspend fun hasAlbums() = albumsQueries.albumsCount().awaitSingle() > 0

    override fun observeAlbumsByDate() : Flow<Group<String, GetAlbums>> =
        albumsQueries.getAlbums(limit = -1).asFlow()
            .onStart {
                if (allAlbums.items.isEmpty()) {
                    emitAll(albumsQueries.getAlbums(limit = 100).asFlow().take(1))
                }
            }
            .mapToList().groupBy(GetAlbums::id)
            .onStart {
                if (allAlbums.items.isNotEmpty()) {
                    emit(allAlbums)
                }
            }
            .onEach {
                allAlbums = it
            }
            .distinctUntilChanged()

    override suspend fun getAlbumsByDate() : Group<String, GetAlbums> =
        albumsQueries.getAlbums(limit = -1).await().groupBy(GetAlbums::id).let(::Group)

    override fun observePersonAlbums(personId: Int) : Flow<Group<String, GetPersonAlbums>> =
        albumsQueries.getPersonAlbums(personId).asFlow().mapToList().groupBy(GetPersonAlbums::id)
            .distinctUntilChanged()
            .safelyOnStartIgnoring {
                downloadPersonAlbums(personId)
            }

    override suspend fun getPersonAlbums(personId: Int) : Group<String, GetPersonAlbums> =
        albumsQueries.getPersonAlbums(personId).await().groupBy(GetPersonAlbums::id).let(::Group)

    override fun observeAutoAlbums(): Flow<List<AutoAlbums>> =
        autoAlbumsQueries.getAutoAlbums().asFlow().mapToList()
            .distinctUntilChanged()

    override suspend fun getAutoAlbums(): List<AutoAlbums> =
        autoAlbumsQueries.getAutoAlbums().await()

    override fun observeUserAlbums(): Flow<List<UserAlbums>> =
        userAlbumsQueries.getUserAlbums().asFlow().mapToList()
            .distinctUntilChanged()

    override suspend fun getUserAlbums(): List<UserAlbums> =
        userAlbumsQueries.getUserAlbums().await()

    override fun observeAutoAlbum(albumId: Int): Flow<List<GetAutoAlbum>> =
        autoAlbumQueries.getAutoAlbum(albumId.toString()).asFlow().mapToList()
            .distinctUntilChanged()

    override suspend fun getAutoAlbum(albumId: Int): Group<String, GetAutoAlbum> =
        autoAlbumQueries.getAutoAlbum(albumId.toString()).await().groupBy(GetAutoAlbum::id).let(::Group)

    override suspend fun getUserAlbum(albumId: Int): Group<String, GetUserAlbum> =
        userAlbumQueries.getUserAlbum(albumId.toString()).await().groupBy(GetUserAlbum::id).let(::Group)

    override fun observeUserAlbum(albumId: Int): Flow<List<GetUserAlbum>> =
        userAlbumQueries.getUserAlbum(albumId.toString()).asFlow().mapToList()
            .distinctUntilChanged()

    override fun observeAutoAlbumPeople(albumId: Int): Flow<List<GetPeopleForAutoAlbum>> =
        autoAlbumPeopleQueries.getPeopleForAutoAlbum(albumId.toString())
            .asFlow().mapToList()

    override suspend fun refreshAutoAlbums() {
        val albums = albumsService.getAutoAlbums()
        autoAlbumsQueries.transaction {
            autoAlbumsQueries.clear()
            for (album in albums.results) {
                autoAlbumsQueries.insert(album.toAutoAlbums())
            }
        }
    }

    override suspend fun refreshUserAlbums() {
        val albums = albumsService.getUserAlbums()
        userAlbumsQueries.transaction {
            userAlbumsQueries.clear()
            for (album in albums.results) {
                userAlbumsQueries.insert(album.toUserAlbums())
            }
        }
    }

    override suspend fun refreshAutoAlbum(albumId: Int) {
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

    override suspend fun refreshUserAlbum(albumId: Int) {
        val album = albumsService.getUserAlbum(albumId.toString())
        db.transaction {
            userAlbumQueries.insert(
                UserAlbum(
                    id = albumId.toString(),
                    title = album.title,
                    date = album.date,
                    location = album.location,
                )
            )
            userAlbumPhotosQueries.removePhotosForAlbum(albumId.toString())
            for (photo in album.groups.flatMap { it.photos }) {
                photoSummaryQueries.insert(photo.toPhotoSummary(albumId.toString()))
                userAlbumPhotosQueries.insert(photo.id, albumId.toString())
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

    override suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit) {
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