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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.api.db.media.RemoteMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.media.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.api.db.media.RemoteMediaTrashQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.RemoteMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker.RemoteMediaItemWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class RemoteMediaRepository @Inject constructor(
    private val remoteMediaItemDetailsQueries: RemoteMediaItemDetailsQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    private val remoteMediaItemWorkScheduler: RemoteMediaItemWorkScheduler,
    private val remoteMediaService: RemoteMediaService,
    private val remoteMediaTrashQueries: RemoteMediaTrashQueries,
) {

    fun observeAllMediaItemDetails(): Flow<List<DbRemoteMediaItemDetails>> =
        remoteMediaItemDetailsQueries.getAll(limit = -1).asFlow()
            .onStart {
                emitAll(remoteMediaItemDetailsQueries.getAll(limit = 100).asFlow().take(1))
            }
            .mapToList()

    fun observeFavouriteMedia(favouriteThreshold: Int): Flow<List<DbRemoteMediaItemSummary>> =
        remoteMediaItemSummaryQueries.getFavourites(favouriteThreshold).asFlow()
            .mapToList()

    fun observeHiddenMedia(): Flow<List<DbRemoteMediaItemSummary>> =
        remoteMediaItemSummaryQueries.getHidden().asFlow()
            .mapToList()

    suspend fun getMediaItemDetails(id: String): DbRemoteMediaItemDetails? =
        remoteMediaItemDetailsQueries.getMediaItem(id).awaitSingleOrNull()

    suspend fun getFavouriteMedia(favouriteThreshold: Int): List<DbRemoteMediaItemSummary> =
        remoteMediaItemSummaryQueries.getFavourites(favouriteThreshold).await()

    suspend fun getFavouriteMediaCount(favouriteThreshold: Int): Long =
        remoteMediaItemSummaryQueries.countFavourites(favouriteThreshold).awaitSingle()

    suspend fun getHiddenMedia(): List<DbRemoteMediaItemSummary> =
        remoteMediaItemSummaryQueries.getHidden().await()

    suspend fun refreshDetailsIfMissing(id: String) {
        when (getMediaItemDetails(id)) {
            null -> {
                refreshDetails(id)
            }
        }
    }

    suspend fun refreshDetailsNowIfMissing(id: String) =
        when (getMediaItemDetails(id)) {
            null -> refreshDetailsNow(id)
            else -> Result.success(Unit)
        }

    suspend fun refreshDetailsNow(id: String) = runCatchingWithLog {
        remoteMediaService.getMediaItem(id).toDbModel().let {
            insertMediaItem(it)
        }
    }

    fun refreshDetails(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemDetailsRetrieve(id)
    }

    suspend fun refreshFavourites(favouriteThreshold: Int) = runCatchingWithLog {
        val currentFavouriteIds = remoteMediaItemSummaryQueries.getFavourites(favouriteThreshold)
            .await()
            .map { it.id }
            .toSet()
        val newFavourites = remoteMediaService.getFavouriteMedia().results.flatMap { it.items }
        val newFavouriteIds = newFavourites.map { it.id }.toSet()
        newFavourites.forEach {
            async { remoteMediaItemSummaryQueries.setRating(it.rating, it.id) }
        }
        (currentFavouriteIds - newFavouriteIds).forEach {
            val rating = remoteMediaService.getMediaItem(it).rating
            async { remoteMediaItemSummaryQueries.setRating(rating, it) }
        }
    }

    suspend fun refreshHidden(): Result<Unit> = runCatchingWithLog {
        val hidden = remoteMediaService.getHiddenMedia().results.flatMap { it.items }
        async {
            hidden.forEach {
                remoteMediaItemSummaryQueries.insertHidden(
                    id = it.id,
                    dominantColor = it.dominantColor,
                    aspectRatio = it.aspectRatio,
                    location = it.location,
                    rating = it.rating,
                    url = it.url,
                    date = it.date,
                    birthTime = it.birthTime,
                    type = it.type,
                    videoLength = it.videoLength,
                )
            }
        }
    }

    suspend fun insertMediaItem(mediaItemDetails: DbRemoteMediaItemDetails) {
        async {
            remoteMediaItemDetailsQueries.insert(mediaItemDetails)
            remoteMediaItemSummaryQueries.setRating(
                mediaItemDetails.rating,
                mediaItemDetails.imageHash
            )
        }
    }

    suspend fun setMediaItemRating(id: String, rating: Int) {
        async {
            remoteMediaItemDetailsQueries.setRating(rating, id)
            remoteMediaItemSummaryQueries.setRating(rating, id)
        }
    }

    suspend fun trashMediaItem(id: String) {
        async {
            remoteMediaItemSummaryQueries.transaction {
                remoteMediaItemSummaryQueries.copyToTrash(id)
                remoteMediaItemSummaryQueries.delete(id)
            }
        }
    }

    suspend fun restoreMediaItemFromTrash(id: String) {
        async {
            remoteMediaTrashQueries.moveToSummaries(id)
        }
    }

    suspend fun deleteMediaItem(id: String) {
        async {
            remoteMediaItemDetailsQueries.delete(id)
            remoteMediaItemSummaryQueries.delete(id)
            remoteMediaTrashQueries.delete(id)
        }
    }
}