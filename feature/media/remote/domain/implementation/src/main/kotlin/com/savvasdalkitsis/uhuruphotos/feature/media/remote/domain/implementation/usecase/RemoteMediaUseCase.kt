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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrashQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResult
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteFeedDayResult
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteFeedResult
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaDaySummaries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.RemoteMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaHashOperationResponseServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaItemDeleteRequestServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaItemDeletedRequestServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaOperationResponseServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker.RemoteMediaItemWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.andThenTry
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.mapToResultFlow
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class RemoteMediaUseCase @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val remoteMediaRepository: RemoteMediaRepository,
    private val remoteMediaService: RemoteMediaService,
    private val remoteMediaItemWorkScheduler: RemoteMediaItemWorkScheduler,
    private val remoteMediaTrashQueries: RemoteMediaTrashQueries,
    private val userUseCase: UserUseCase,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    private val db: Database,
) : RemoteMediaUseCase {

    override fun observeAllRemoteMediaDetails(): Flow<List<DbRemoteMediaItemDetails>> =
        remoteMediaRepository.observeAllMediaItemDetails()

    override fun observeFavouriteRemoteMedia(): Flow<Result<List<DbRemoteMediaItemSummary>, Throwable>> =
        flow {
            emitAll(
                withFavouriteThreshold { threshold ->
                    remoteMediaRepository.observeFavouriteMedia(threshold)
                }.mapToResultFlow()
            )
        }

    override fun observeHiddenRemoteMedia(): Flow<List<DbRemoteMediaItemSummary>> =
        remoteMediaRepository.observeHiddenMedia()

    override fun observeRemoteMediaItemDetails(id: String): Flow<DbRemoteMediaItemDetails> =
        remoteMediaRepository.observeMediaItemDetails(id).distinctUntilChanged()

    override suspend fun getRemoteMediaItemSummary(id: String): Result<RemoteMediaItemSummaryStatus, Throwable> =
        remoteMediaRepository.getOrRefreshRemoteMediaItemSummary(id)

    override suspend fun getFavouriteMediaSummariesCount(): Result<Long, Throwable> = withFavouriteThreshold {
        remoteMediaRepository.getFavouriteMediaCount(it)
    }

    override suspend fun getHiddenMediaSummaries(): List<DbRemoteMediaItemSummary> =
        remoteMediaRepository.getHiddenMedia()

    override suspend fun setMediaItemFavourite(id: String, favourite: Boolean): SimpleResult =
        userUseCase.getRemoteUserOrRefresh().andThenTry {
            remoteMediaRepository.setMediaItemRating(id, it.favoriteMinRating?.takeIf { favourite } ?: 0)
            remoteMediaItemWorkScheduler.scheduleMediaItemFavourite(id, favourite)
        }.simple()

    override suspend fun refreshDetailsNowIfMissing(id: String): Result<MediaOperationResult, Throwable> =
        remoteMediaRepository.refreshDetailsNowIfMissing(id)

    override suspend fun refreshDetailsNow(id: String): SimpleResult =
        remoteMediaRepository.refreshDetailsNow(id)

    override suspend fun refreshFavouriteMedia(): SimpleResult =
        resultWithFavouriteThreshold { favouriteThreshold ->
            val currentFavourites = remoteMediaRepository.getFavouriteMedia(favouriteThreshold)
                .map { it.id }
                .toSet()
            val newFavourites = mutableListOf<RemoteMediaItemSummary>()
            refreshMediaCollections(
                incompleteMediaCollections = { remoteMediaService.getFavouriteMedia() },
                clearCollectionsBeforeRefreshing = {},
                completeMediaCollection = { id ->
                    remoteMediaService.getFavouriteMediaCollection(id).also { collection ->
                        newFavourites += collection.results.items
                    }
                },
                processSummary = { albumId, summary ->
                    remoteMediaRepository.saveRemoteMediaSummary(albumId, summary)
                }
            )
            val newFavouriteIds = newFavourites.map { it.id }.toSet()
            runCatchingWithLog {
                (currentFavourites - newFavouriteIds).forEach {
                    val rating = remoteMediaService.getMediaItem(it).rating
                    async { remoteMediaItemSummaryQueries.setRating(rating, it) }
                }
            }
        }

    override fun saveRemoteMediaSummary(containerId: String, summary: RemoteMediaItemSummary) {
        remoteMediaRepository.saveRemoteMediaSummary(containerId, summary)
    }

    override suspend fun refreshHiddenMedia() = refreshMediaCollections(
        incompleteMediaCollections = { remoteMediaService.getHiddenMedia() },
        clearCollectionsBeforeRefreshing = { remoteMediaItemSummaryQueries.deleteHidden() },
        completeMediaCollection = { remoteMediaService.getHiddenMediaCollection(it) },
        processSummary = { _, summary ->
            remoteMediaItemSummaryQueries.insertHidden(
                id = summary.id,
                dominantColor = summary.dominantColor,
                aspectRatio = summary.aspectRatio,
                location = summary.location,
                rating = summary.rating,
                url = summary.url,
                date = summary.date,
                birthTime = summary.birthTime,
                type = summary.type,
                videoLength = summary.videoLength,
                gpsLat = summary.lat,
                gpsLon = summary.lon,
            )
        }
    )

    override fun trashMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemTrashing(id)
    }

    override suspend fun trashMediaItemNow(id: String): Boolean {
        val response = remoteMediaService.setMediaItemDeleted(
            RemoteMediaItemDeletedRequestServiceModel(
                mediaHashes = listOf(id),
                deleted = true,
            )
        )
        return if (shouldTrashLocally(response)) {
            remoteMediaRepository.trashMediaItem(id)
            true
        } else if (shouldDeleteLocally(response)) {
            remoteMediaRepository.deleteMediaItem(id)
            true
        } else {
            false
        }
    }

    private fun shouldTrashLocally(response: Response<RemoteMediaOperationResponseServiceModel>) =
        response.code() in 200..299 && response.body()?.status == true

    private fun shouldDeleteLocally(response: Response<RemoteMediaOperationResponseServiceModel>) =
        response.code() == 404

    override fun deleteMediaItems(vararg ids: String) {
        for (id in ids) {
            remoteMediaItemWorkScheduler.scheduleMediaItemDeletion(id)
        }
    }

    override suspend fun deleteMediaItemNow(id: String): Boolean {
        val response = remoteMediaService.deleteMediaItemPermanently(
            RemoteMediaItemDeleteRequestServiceModel(
                mediaHashes = listOf(id),
            )
        )
        if (shouldDeleteLocallyHashed(response)) {
            remoteMediaRepository.deleteMediaItem(id)
            return true
        }
        return false
    }

    private fun shouldDeleteLocallyHashed(response: Response<RemoteMediaHashOperationResponseServiceModel>) =
        response.code() == 404 ||
                (response.code() in 200..299 && response.body()?.status == true)


    override fun restoreMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemRestoration(id)
    }

    override suspend fun processRemoteMediaCollections(
        incompleteAlbumsFetcher: suspend () -> List<RemoteMediaDaySummaries.Incomplete>,
        completeAlbumsFetcher: suspend (String) -> RemoteMediaDaySummaries.Complete,
        onProgressChange: suspend (current: Int, total: Int) -> Unit,
        incompleteAlbumsProcessor: suspend (List<RemoteMediaDaySummaries.Incomplete>) -> Unit,
        completeAlbumInterceptor: suspend (RemoteMediaDaySummaries.Complete) -> Unit,
        clearSummariesBeforeInserting: Boolean,
    ): SimpleResult = runCatchingWithLog {
        onProgressChange(0, 0)
        val albums = incompleteAlbumsFetcher()
        incompleteAlbumsProcessor(albums)
        for ((index, incompleteAlbum) in albums.withIndex()) {
            val id = incompleteAlbum.id
            updateSummaries(id, completeAlbumsFetcher, completeAlbumInterceptor, clearSummariesBeforeInserting)
            onProgressChange(index, albums.size)
        }
    }.simple()

    override suspend fun exists(hash: MediaItemHash): Result<Boolean, Throwable> = runCatchingWithLog {
        remoteMediaService.exists(hash.hash).exists
    }

    override suspend fun refreshMediaCollections(
        incompleteMediaCollections: suspend () -> RemoteFeedResult,
        clearCollectionsBeforeRefreshing: () -> Unit,
        completeMediaCollection: suspend (String) -> RemoteFeedDayResult,
        processSummary: (albumId: String, summary: RemoteMediaItemSummary) -> Unit,
    ): SimpleResult = runCatchingWithLog {
        val incompleteCollections = incompleteMediaCollections().results
        async {
            db.transaction {
                for (incompleteCollection in incompleteCollections) {
                    remoteMediaRepository.insertMediaCollection(incompleteCollection.toDbModel())
                }
            }
        }
        async { clearCollectionsBeforeRefreshing() }
        for (incompleteCollection in incompleteCollections) {
            val id = incompleteCollection.id
            val completeCollection = completeMediaCollection(id).results
            async {
                completeCollection.items.forEach {
                    processSummary(id, it)
                }
            }
        }
    }.simple()

    private suspend fun updateSummaries(
        id: String,
        remoteMediaDaySummariesFetcher: suspend (String) -> RemoteMediaDaySummaries.Complete,
        completeAlbumInterceptor: suspend (RemoteMediaDaySummaries.Complete) -> Unit,
        clearSummariesBeforeInserting: Boolean,
    ) {
        val completeAlbum = remoteMediaDaySummariesFetcher(id)
        completeAlbumInterceptor(completeAlbum)
        if (clearSummariesBeforeInserting) {
            remoteMediaItemSummaryQueries.deletePhotoSummariesforAlbum(id)
        }
        saveRemoteMediaSummaries(completeAlbum)
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T, Throwable> =
        userUseCase.getRemoteUserOrRefresh().andThenTry {
            action(it.favoriteMinRating!!)
        }

    private suspend fun resultWithFavouriteThreshold(action: suspend (Int) -> SimpleResult): SimpleResult =
        userUseCase.getRemoteUserOrRefresh().andThenTry {
            action(it.favoriteMinRating!!).getOrThrow()
        }

    private fun saveRemoteMediaSummaries(album: RemoteMediaDaySummaries.Complete) {
        db.transaction {
            for (day in album.items) {
                saveRemoteMediaSummary(album.id, day)
            }
        }
    }
}