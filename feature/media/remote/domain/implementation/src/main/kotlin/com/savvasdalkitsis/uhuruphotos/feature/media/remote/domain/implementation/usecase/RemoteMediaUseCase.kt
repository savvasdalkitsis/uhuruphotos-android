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
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaCollectionsFilterModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDayCompleteResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDaySummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaCollectionsRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.RemoteMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.request.RemoteMediaItemDeleteRequestData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.request.RemoteMediaItemDeletedRequestData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteMediaHashOperationResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteMediaOperationResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker.RemoteMediaItemWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.async
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
    private val remoteMediaRepository: RemoteMediaRepository,
    private val remoteMediaCollectionsRepository: RemoteMediaCollectionsRepository,
    private val remoteMediaService: RemoteMediaService,
    private val remoteMediaItemWorkScheduler: RemoteMediaItemWorkScheduler,
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

    override suspend fun refreshDetailsNowIfMissing(id: String): Result<MediaOperationResultModel, Throwable> =
        remoteMediaRepository.refreshDetailsNowIfMissing(id)

    override suspend fun refreshDetailsNow(id: String): SimpleResult =
        remoteMediaRepository.refreshDetailsNow(id)

    override suspend fun refreshFavouriteMedia(): SimpleResult =
        resultWithFavouriteThreshold { favouriteThreshold -> refreshFavourites(favouriteThreshold) }

    private suspend fun refreshFavourites(favouriteThreshold: Int): SimpleResult {
        val currentFavourites = remoteMediaRepository.getFavouriteMedia(favouriteThreshold)
            .map { it.id }
            .toSet()
        val newFavourites = mutableSetOf<String>()
        refreshCollections(
            filter = RemoteMediaCollectionsFilterModel.Favourites,
            clearCollectionsBeforeRefreshing = {},
            onCollection = { collection -> newFavourites += collection.items.map { it.id } },
            processSummary = remoteMediaRepository::saveRemoteMediaSummary,
        )
        return refreshRatings(currentFavourites - newFavourites)
    }

    private suspend fun refreshRatings(ids: Set<String>): SimpleResult = runCatchingWithLog {
        ids.forEach {
            val rating = remoteMediaService.getMediaItem(it).rating
            async { remoteMediaItemSummaryQueries.setRating(rating, it) }
        }
    }

    override fun saveRemoteMediaSummary(containerId: String, summary: RemoteMediaItemSummaryResponse) {
        remoteMediaRepository.saveRemoteMediaSummary(containerId, summary)
    }

    override suspend fun refreshHiddenMedia() = refreshMediaCollections(
        filter = RemoteMediaCollectionsFilterModel.Hidden,
        clearCollectionsBeforeRefreshing = { remoteMediaItemSummaryQueries.deleteHidden() },
        processSummary = { _, summary ->
            remoteMediaItemSummaryQueries.insert(summary.toDbModel(HIDDEN_CONTAINER_ID))
        }
    )

    override fun trashMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemTrashing(id)
    }

    override suspend fun trashMediaItemNow(id: String): Boolean {
        val response = remoteMediaService.setMediaItemDeleted(
            RemoteMediaItemDeletedRequestData(
                mediaHashes = listOf(remoteMediaRepository.contentHashOf(id)),
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

    private fun shouldTrashLocally(response: Response<RemoteMediaOperationResponseData>) =
        response.code() in 200..299 && response.body()?.status == true

    private fun shouldDeleteLocally(response: Response<RemoteMediaOperationResponseData>) =
        response.code() == 404

    override fun deleteMediaItems(vararg ids: String) {
        for (id in ids) {
            remoteMediaItemWorkScheduler.scheduleMediaItemDeletion(id)
        }
    }

    override suspend fun deleteMediaItemNow(id: String): Boolean {
        val response = remoteMediaService.deleteMediaItemPermanently(
            RemoteMediaItemDeleteRequestData(
                mediaHashes = listOf(remoteMediaRepository.contentHashOf(id)),
            )
        )
        if (shouldDeleteLocallyHashed(response)) {
            remoteMediaRepository.deleteMediaItem(id)
            return true
        }
        return false
    }

    private fun shouldDeleteLocallyHashed(response: Response<RemoteMediaHashOperationResponseData>) =
        response.code() == 404 ||
                (response.code() in 200..299 && response.body()?.status == true)


    override fun restoreMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemRestoration(id)
    }

    override suspend fun processRemoteMediaCollections(
        remoteMediaDaySummaryResponseAlbumsFetcher: suspend () -> List<RemoteMediaDaySummaryResponse>,
        remoteMediaDayCompleteResponseAlbumsFetcher: suspend (String) -> RemoteMediaDayCompleteResponse,
        onProgressChange: suspend (current: Int, total: Int) -> Unit,
        incompleteAlbumsProcessor: suspend (List<RemoteMediaDaySummaryResponse>) -> Unit,
        completeAlbumInterceptor: suspend (RemoteMediaDayCompleteResponse) -> Unit,
        clearSummariesBeforeInserting: Boolean,
    ): SimpleResult = runCatchingWithLog {
        onProgressChange(0, 0)
        val albums = remoteMediaDaySummaryResponseAlbumsFetcher()
        incompleteAlbumsProcessor(albums)
        for ((index, incompleteAlbum) in albums.withIndex()) {
            val id = incompleteAlbum.id
            updateSummaries(id, remoteMediaDayCompleteResponseAlbumsFetcher, completeAlbumInterceptor, clearSummariesBeforeInserting)
            onProgressChange(index, albums.size)
        }
    }.simple()

    override suspend fun exists(hash: MediaItemHashModel): Result<Boolean, Throwable> = runCatchingWithLog {
        remoteMediaService.exists(hash.hash).exists
    }

    override suspend fun getRemoteMediaCollections(
        filter: RemoteMediaCollectionsFilterModel,
    ): List<RemoteMediaDaySummaryResponse> = remoteMediaCollectionsRepository.getCollections(filter)

    override suspend fun getRemoteMediaCollection(
        id: String,
        filter: RemoteMediaCollectionsFilterModel,
    ): RemoteMediaDayCompleteResponse = remoteMediaCollectionsRepository.getCollection(id, filter)

    override suspend fun refreshMediaCollections(
        filter: RemoteMediaCollectionsFilterModel,
        clearCollectionsBeforeRefreshing: () -> Unit,
        processSummary: (albumId: String, summary: RemoteMediaItemSummaryResponse) -> Unit,
    ): SimpleResult = refreshCollections(filter, clearCollectionsBeforeRefreshing, {}, processSummary)

    private suspend fun refreshCollections(
        filter: RemoteMediaCollectionsFilterModel,
        clearCollectionsBeforeRefreshing: () -> Unit,
        onCollection: (RemoteMediaDayCompleteResponse) -> Unit,
        processSummary: (albumId: String, summary: RemoteMediaItemSummaryResponse) -> Unit,
    ): SimpleResult = runCatchingWithLog {
        val incompleteCollections = getRemoteMediaCollections(filter)
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
            val completeCollection = getRemoteMediaCollection(id, filter)
            onCollection(completeCollection)
            async {
                completeCollection.items.forEach {
                    processSummary(id, it)
                }
            }
        }
    }.simple()

    private suspend fun updateSummaries(
        id: String,
        remoteMediaDaySummariesFetcher: suspend (String) -> RemoteMediaDayCompleteResponse,
        completeAlbumInterceptor: suspend (RemoteMediaDayCompleteResponse) -> Unit,
        clearSummariesBeforeInserting: Boolean,
    ) {
        val completeAlbum = remoteMediaDaySummariesFetcher(id)
        completeAlbumInterceptor(completeAlbum)
        if (clearSummariesBeforeInserting && !completeAlbum.incomplete) {
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

    private fun saveRemoteMediaSummaries(album: RemoteMediaDayCompleteResponse) {
        db.transaction {
            for (day in album.items) {
                saveRemoteMediaSummary(album.id, day)
            }
        }
    }

    private companion object {
        const val HIDDEN_CONTAINER_ID = "hiddenPhoto"
    }
}
