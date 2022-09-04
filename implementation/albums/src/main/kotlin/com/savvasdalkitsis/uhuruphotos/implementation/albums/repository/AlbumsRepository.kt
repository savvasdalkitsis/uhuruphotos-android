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
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.toAutoAlbums
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.await
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import javax.inject.Inject

internal class AlbumsRepository @Inject constructor(
    private val db: Database,
    private val albumsService: AlbumsService,
    private val remoteMediaCollectionsQueries: RemoteMediaCollectionsQueries,
    private val autoAlbumsQueries: AutoAlbumsQueries,
    private val autoAlbumQueries: AutoAlbumQueries,
    private val autoAlbumPhotosQueries: AutoAlbumPhotosQueries,
    private val autoAlbumPeopleQueries: AutoAlbumPeopleQueries,
    private val peopleQueries: PeopleQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    private val remoteMediaItemDetailsQueries: RemoteMediaItemDetailsQueries,
    private val settingsUseCase: SettingsUseCase,
) : AlbumsRepository {

    private var allAlbums: Group<String, GetRemoteMediaCollections> = Group(emptyMap())

    override suspend fun hasAlbums() =
        remoteMediaCollectionsQueries.remoteMediaCollectionCount().awaitSingle() > 0

    override fun observeAlbumsByDate() : Flow<Group<String, GetRemoteMediaCollections>> =
        remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = -1).asFlow()
            .onStart {
                if (allAlbums.items.isEmpty()) {
                    emitAll(remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = 100).asFlow().take(1))
                }
            }
            .mapToList().groupBy(GetRemoteMediaCollections::id)
            .onStart {
                if (allAlbums.items.isNotEmpty()) {
                    emit(allAlbums)
                }
            }
            .onEach {
                allAlbums = it
            }
            .distinctUntilChanged()

    override suspend fun getAlbumsByDate() : Group<String, GetRemoteMediaCollections> =
        remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = -1).await().groupBy(GetRemoteMediaCollections::id).let(::Group)

    override suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit) =
        process(
            albumsFetcher = { albumsService.getAlbumsByDate() },
            albumFetcher = getAlbumAllPages(),
            shallow = shallow,
            onProgressChange = onProgressChange,
            incompleteAlbumsProcessor = { albums ->
                remoteMediaCollectionsQueries.transaction {
                    remoteMediaCollectionsQueries.clearAll()
                    for (album in albums.map { it.toDbModel() }) {
                        remoteMediaCollectionsQueries.insert(album)
                    }
                }
            }
        )

    override suspend fun refreshAlbum(albumId: String) {
        process(
            albumsFetcher = { AlbumsByDate(
                results = listOf(Album.IncompleteAlbum(albumId, null, "", true, 1))
            ) },
            albumFetcher = getAlbumAllPages(),
            shallow = false,
        )
    }

    override fun observeAutoAlbums(): Flow<List<AutoAlbums>> =
        autoAlbumsQueries.getAutoAlbums().asFlow().mapToList()
            .distinctUntilChanged()

    override suspend fun getAutoAlbums(): List<AutoAlbums> =
        autoAlbumsQueries.getAutoAlbums().await()

    override fun observeAutoAlbum(albumId: Int): Flow<List<GetAutoAlbum>> =
        autoAlbumQueries.getAutoAlbum(albumId.toString()).asFlow().mapToList()
            .distinctUntilChanged()

    override suspend fun getAutoAlbum(albumId: Int): Group<String, GetAutoAlbum> =
        autoAlbumQueries.getAutoAlbum(albumId.toString()).await().groupBy(GetAutoAlbum::id).let(::Group)

    override fun observeAutoAlbumPeople(albumId: Int): Flow<List<GetPeopleForAutoAlbum>> =
        autoAlbumPeopleQueries.getPeopleForAutoAlbum(albumId.toString())
            .asFlow().mapToList()

    override suspend fun refreshAutoAlbums() = runCatchingWithLog {
        val albums = albumsService.getAutoAlbums()
        autoAlbumsQueries.transaction {
            autoAlbumsQueries.clearAll()
            for (album in albums.results) {
                autoAlbumsQueries.insert(album.toAutoAlbums())
            }
        }
    }

    override suspend fun refreshAutoAlbum(albumId: Int): Result<Unit> = runCatchingWithLog {
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
                peopleQueries.insertPerson(person.toDbModel())
                autoAlbumPeopleQueries.insert(person.id, albumId.toString())
            }
            autoAlbumPhotosQueries.removePhotosForAlbum(albumId.toString())
            for (photo in album.photos) {
                remoteMediaItemDetailsQueries.insert(photo.toDbModel())
                autoAlbumPhotosQueries.insert(photo.imageHash, albumId.toString())
            }
        }
    }

    private fun getAlbumAllPages(): suspend (String) -> Album.CompleteAlbum = { id ->
        var page = 1
        val albums = mutableListOf<Album.CompleteAlbum>()
        do {
            val album = albumsService.getAlbum(id, page).results
            albums += album
            page++
        } while (albums.sumOf { it.items.size } < album.numberOfItems)
        albums.reduce { acc, completeAlbum ->
            acc.copy(
                items = acc.items + completeAlbum.items
            )
        }
    }

    private suspend fun process(
        albumsFetcher: suspend () -> AlbumsByDate,
        albumFetcher: suspend (String) -> Album.CompleteAlbum,
        shallow: Boolean,
        onProgressChange: suspend (Int) -> Unit = {},
        incompleteAlbumsProcessor: suspend (List<Album.IncompleteAlbum>) -> Unit = {},
        completeAlbumProcessor: suspend (Album.CompleteAlbum) -> Unit = {},
    ): Result<Unit> = runCatchingWithLog {
        onProgressChange(0)
        val albums = albumsFetcher()
        incompleteAlbumsProcessor(albums.results)
        val albumsToDownloadSummaries = when {
            shallow -> albums.results.take(settingsUseCase.getFeedDaysToRefresh())
            else -> albums.results
        }
        for ((index, incompleteAlbum) in albumsToDownloadSummaries.withIndex()) {
            val id = incompleteAlbum.id
            updateSummaries(id, albumFetcher, completeAlbumProcessor)
            onProgressChange((100 * ((index + 1) / albumsToDownloadSummaries.size.toFloat())).toInt())
        }
    }

    private suspend fun updateSummaries(
        id: String,
        albumFetcher: suspend (String) -> Album.CompleteAlbum,
        completeAlbumProcessor: suspend (Album.CompleteAlbum) -> Unit,
    ) {
        val completeAlbum = albumFetcher(id)
        completeAlbumProcessor(completeAlbum)
        async {
            remoteMediaItemSummaryQueries.transaction {
                remoteMediaItemSummaryQueries.deletePhotoSummariesforAlbum(id)
                completeAlbum.items
                    .map { it.toDbModel(id) }
                    .forEach {
                        remoteMediaItemSummaryQueries.insert(it)
                    }
            }
        }
    }
}