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
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResult
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.RemoteMediaService
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
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class RemoteMediaUseCase @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val remoteMediaRepository: RemoteMediaRepository,
    private val remoteMediaService: RemoteMediaService,
    private val remoteMediaItemWorkScheduler: RemoteMediaItemWorkScheduler,
    private val userUseCase: UserUseCase,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
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

    override suspend fun getRemoteMediaItemDetails(id: String): DbRemoteMediaItemDetails? =
        remoteMediaRepository.getMediaItemDetails(id)

    override suspend fun getFavouriteMediaSummariesCount(): Result<Long, Throwable> = withFavouriteThreshold {
        remoteMediaRepository.getFavouriteMediaCount(it)
    }

    override suspend fun getHiddenMediaSummaries(): List<DbRemoteMediaItemSummary> =
        remoteMediaRepository.getHiddenMedia()

    override suspend fun setMediaItemFavourite(id: String, favourite: Boolean): SimpleResult =
        userUseCase.getUserOrRefresh().andThenTry {
            remoteMediaRepository.setMediaItemRating(id, it.favoriteMinRating?.takeIf { favourite } ?: 0)
            remoteMediaItemWorkScheduler.scheduleMediaItemFavourite(id, favourite)
        }.simple()

    override suspend fun refreshDetailsNowIfMissing(id: String): Result<MediaOperationResult, Throwable> =
        remoteMediaRepository.refreshDetailsNowIfMissing(id)

    override suspend fun refreshDetailsNow(id: String): SimpleResult =
        remoteMediaRepository.refreshDetailsNow(id)

    override suspend fun refreshFavouriteMedia(): SimpleResult =
        resultWithFavouriteThreshold {
            remoteMediaRepository.refreshFavourites(it)
        }

    override suspend fun refreshHiddenMedia() =
        remoteMediaRepository.refreshHidden()

    override fun trashMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemTrashing(id)
    }

    override fun deleteMediaItems(vararg ids: String) {
        for (id in ids) {
            remoteMediaItemWorkScheduler.scheduleMediaItemDeletion(id)
        }
    }

    override fun restoreMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemRestoration(id)
    }

    override suspend fun processRemoteMediaCollections(
        albumsFetcher: suspend () -> RemoteMediaCollectionsByDate,
        remoteMediaCollectionFetcher: suspend (String) -> RemoteMediaCollection.Complete,
        shallow: Boolean,
        onProgressChange: suspend (current: Int, total: Int) -> Unit,
        incompleteAlbumsProcessor: suspend (List<RemoteMediaCollection.Incomplete>) -> Unit,
        completeAlbumProcessor: suspend (RemoteMediaCollection.Complete) -> Unit,
        clearSummariesBeforeInserting: Boolean,
    ): SimpleResult = runCatchingWithLog {
        onProgressChange(0, 0)
        val albums = albumsFetcher()
        incompleteAlbumsProcessor(albums.results)
        val albumsToDownloadSummaries = when {
            shallow -> albums.results.take(settingsUseCase.getFeedDaysToRefresh())
            else -> albums.results
        }
        for ((index, incompleteAlbum) in albumsToDownloadSummaries.withIndex()) {
            val id = incompleteAlbum.id
            updateSummaries(id, remoteMediaCollectionFetcher, completeAlbumProcessor, clearSummariesBeforeInserting)
            onProgressChange(index, albumsToDownloadSummaries.size)
        }
    }.simple()

    override suspend fun exists(hash: MediaItemHash): Result<Boolean, Throwable> = runCatchingWithLog {
        remoteMediaService.exists(hash.value).exists
    }

    private suspend fun updateSummaries(
        id: String,
        remoteMediaCollectionFetcher: suspend (String) -> RemoteMediaCollection.Complete,
        completeAlbumProcessor: suspend (RemoteMediaCollection.Complete) -> Unit,
        clearSummariesBeforeInserting: Boolean = true,
    ) {
        val completeAlbum = remoteMediaCollectionFetcher(id)
        completeAlbumProcessor(completeAlbum)
        async {
            remoteMediaItemSummaryQueries.transaction {
                if (clearSummariesBeforeInserting) {
                    remoteMediaItemSummaryQueries.deletePhotoSummariesforAlbum(id)
                }
                completeAlbum.items
                    .map { it.toDbModel(id) }
                    .forEach {
                        remoteMediaItemSummaryQueries.insert(it)
                    }
            }
        }
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T, Throwable> =
        userUseCase.getUserOrRefresh().andThenTry {
            action(it.favoriteMinRating!!)
        }

    private suspend fun resultWithFavouriteThreshold(action: suspend (Int) -> SimpleResult): SimpleResult =
        userUseCase.getUserOrRefresh().andThenTry {
            action(it.favoriteMinRating!!).getOrThrow()
        }
}