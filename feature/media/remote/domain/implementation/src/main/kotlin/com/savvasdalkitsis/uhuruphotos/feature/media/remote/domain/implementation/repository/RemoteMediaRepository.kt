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

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.lightbox.LightboxDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrashQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus.Found
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus.Processing
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryContainerResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.containerId
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.contentHash
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.RemoteMediaService
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class RemoteMediaRepository @Inject constructor(
    private val remoteMediaCollectionsQueries: RemoteMediaCollectionsQueries,
    private val remoteMediaItemDetailsQueries: RemoteMediaItemDetailsQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    private val remoteMediaService: RemoteMediaService,
    private val remoteMediaTrashQueries: RemoteMediaTrashQueries,
    private val lightboxDetailsQueries: LightboxDetailsQueries,
) {

    fun observeAllMediaItemDetails(): Flow<List<DbRemoteMediaItemDetails>> =
        remoteMediaItemDetailsQueries.getAll(limit = -1).asFlow().distinctUntilChanged()
            .onStart {
                emitAll(remoteMediaItemDetailsQueries.getAll(limit = 100).asFlow().take(1))
            }
            .mapToList(Dispatchers.IO)

    fun observeFavouriteMedia(favouriteThreshold: Int): Flow<List<DbRemoteMediaItemSummary>> =
        remoteMediaItemSummaryQueries.getFavourites(favouriteThreshold).asFlow()
            .mapToList(Dispatchers.IO)
            .distinctUntilChanged()

    fun observeHiddenMedia(): Flow<List<DbRemoteMediaItemSummary>> =
        remoteMediaItemSummaryQueries.getHidden().asFlow()
            .mapToList(Dispatchers.IO).distinctUntilChanged()

    fun observeMediaItemDetails(id: String): Flow<DbRemoteMediaItemDetails> =
        remoteMediaItemDetailsQueries.getMediaItem(id).asFlow()
            .mapToOneNotNull(Dispatchers.IO).distinctUntilChanged()

    private suspend fun getMediaItemDetails(id: String): DbRemoteMediaItemDetails? =
        remoteMediaItemDetailsQueries.getMediaItem(id).awaitSingleOrNull()

    suspend fun getOrRefreshRemoteMediaItemSummary(id: String): Result<RemoteMediaItemSummaryStatus, Throwable> =
        when (val summary = remoteMediaItemSummaryQueries.get(id).awaitSingleOrNull()) {
            null -> runCatchingWithLog { remoteMediaService.getMediaItemSummary(id).toStatus() }
            else -> Ok(Found(summary.containerId))
        }

    private fun RemoteMediaItemSummaryContainerResponseData.toStatus(): RemoteMediaItemSummaryStatus {
        val containerId = containerId
        return when {
            containerId == null || isProcessing -> Processing(this)
            else -> {
                saveRemoteMediaSummary(containerId, remoteMediaItemSummary)
                Found(containerId)
            }
        }
    }

    private val RemoteMediaItemSummaryContainerResponseData.isProcessing: Boolean
        get() = processing == true || remoteMediaItemSummary.aspectRatio == null

    suspend fun contentHashOf(id: String): String =
        localContentHashOf(id) ?: remoteMediaService.getMediaItem(id).imageHash

    private suspend fun localContentHashOf(id: String): String? =
        summaryContentHashOf(id) ?: getMediaItemDetails(id)?.imageHash

    private suspend fun summaryContentHashOf(id: String): String? =
        remoteMediaItemSummaryQueries.get(id).awaitSingleOrNull()?.url
            ?: remoteMediaTrashQueries.get(id).awaitSingleOrNull()?.url

    suspend fun getFavouriteMedia(favouriteThreshold: Int): List<DbRemoteMediaItemSummary> =
        remoteMediaItemSummaryQueries.getFavourites(favouriteThreshold).awaitList()

    suspend fun getFavouriteMediaCount(favouriteThreshold: Int): Long =
        remoteMediaItemSummaryQueries.countFavourites(favouriteThreshold).awaitSingle()

    suspend fun getHiddenMedia(): List<DbRemoteMediaItemSummary> =
        remoteMediaItemSummaryQueries.getHidden().awaitList()

    suspend fun refreshDetailsNowIfMissing(id: String) =
        when (getMediaItemDetails(id)) {
            null -> refreshDetailsNow(id).map { MediaOperationResultModel.CHANGED }
            else -> Ok(MediaOperationResultModel.SKIPPED)
        }

    suspend fun refreshDetailsNow(id: String) = runCatchingWithLog {
        remoteMediaService.getMediaItem(id).toDbModel().let {
            insertMediaItem(it)
        }
    }

    fun saveRemoteMediaSummary(containerId: String, summary: RemoteMediaItemSummaryResponse) {
        when {
            summary.removed -> deleteMediaItem(summary.id)
            summary.inTrash -> trashMediaItem(summary.id)
            else -> insertRemoteMediaSummary(containerId, summary)
        }
    }

    private fun insertRemoteMediaSummary(containerId: String, summary: RemoteMediaItemSummaryResponse) {
        remoteMediaItemSummaryQueries.insert(summary.toDbModel(containerId))
        val md5 = MediaItemHashModel.fromRemoteMediaHash(summary.contentHash, summary.owner?.id).md5.value
        lightboxDetailsQueries.touch(md5 = md5)
        lightboxDetailsQueries.updateRemoteGps(
            lat = summary.lat?.toDoubleOrNull(),
            lon = summary.lon?.toDoubleOrNull(),
            hash = summary.contentHash,
            md5 = md5,
        )
    }

    fun insertMediaItem(mediaItemDetails: DbRemoteMediaItemDetails) {
        remoteMediaItemDetailsQueries.insert(mediaItemDetails)
        remoteMediaItemSummaryQueries.setRating(
            mediaItemDetails.rating,
            mediaItemDetails.remoteId ?: mediaItemDetails.imageHash
        )
    }

    fun setMediaItemRating(id: String, rating: Int) {
        remoteMediaItemDetailsQueries.setRating(rating, id)
        remoteMediaItemSummaryQueries.setRating(rating, id)
    }

    fun trashMediaItem(id: String) {
        remoteMediaItemSummaryQueries.copyToTrash(id)
        remoteMediaItemSummaryQueries.delete(id)
    }

    fun restoreMediaItemFromTrash(id: String) {
        remoteMediaTrashQueries.moveToSummaries(id)
    }

    fun deleteMediaItem(id: String) {
        remoteMediaItemDetailsQueries.delete(id)
        remoteMediaItemSummaryQueries.delete(id)
        remoteMediaTrashQueries.delete(id)
    }

    fun insertMediaCollection(remoteMediaCollections: DbRemoteMediaCollections) {
        remoteMediaCollectionsQueries.insert(remoteMediaCollections)
    }
}