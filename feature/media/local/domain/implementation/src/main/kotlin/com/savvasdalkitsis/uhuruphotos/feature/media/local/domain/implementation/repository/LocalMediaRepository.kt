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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asFlowList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asFlowSingleNotNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionException
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalMediaRepository @Inject constructor(
    private val database: Database,
    private val localMediaItemDetailsQueries: LocalMediaItemDetailsQueries,
    private val downloadingMediaItemsQueries: DownloadingMediaItemsQueries,
    private val localMediaService: LocalMediaService,
    @PlainTextPreferences
    private val preferences: Preferences,
    private val localMediaResolver: LocalMediaResolver,
    private val localMediaProcessor: LocalMediaProcessor,
) {

    private val keyLocalSyncedBefore = "keyLocalSyncedBefore"

    fun observeMedia(): Flow<List<LocalMediaItemDetails>> = localMediaItemDetailsQueries.getItems()
        .asFlowList()

    fun observeFolder(folderId: Int): Flow<List<LocalMediaItemDetails>> =
        localMediaItemDetailsQueries.getBucketItems(folderId).asFlowList()

    suspend fun getMedia(): List<LocalMediaItemDetails> =
        localMediaItemDetailsQueries.getItems().awaitList()

    suspend fun refreshFolder(folderId: Int) =
         localMediaResolver.resolveMediaForFolder(folderId)
             .processAndInsertItems(folderId)

    suspend fun refresh(
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    ) = localMediaResolver.resolveAllLocalMediaInOrder()
        .processAndInsertItems(onProgressChange = onProgressChange)

    fun clearAll() {
        localMediaItemDetailsQueries.clearAll()
    }

    suspend fun refreshItem(id: Long, video: Boolean) = runCatchingWithLog {
        when {
            video -> localMediaService.getVideosForId(id)
            else -> localMediaService.getPhotosForId(id)
        }.processAndInsertItems(
            removeMissingItems = false,
            forceProcess = true,
        )
    }.simple()

    fun observeItem(id: Long): Flow<LocalMediaItemDetails> =
        localMediaItemDetailsQueries.getItem(id)
            .asFlowSingleNotNull()

    suspend fun getItem(id: Long): LocalMediaItemDetails? =
        localMediaItemDetailsQueries.getItem(id).awaitSingleOrNull()

    fun deletePhotos(vararg ids: Long) = runCatchingWithLog {
        if (localMediaService.deletePhotos(*ids) > 0) {
            removeItemsFromDb(*ids)
        } else {
            throw LocalMediaDeletionException(*ids)
        }
     }.simple()

    fun deleteVideos(vararg ids: Long) = runCatchingWithLog {
        if (localMediaService.deleteVideos(*ids) > 0) {
            removeItemsFromDb(*ids)
        } else {
            throw LocalMediaDeletionException(*ids)
        }
     }.simple()

    fun removeItemsFromDb(vararg ids: Long) =
        localMediaItemDetailsQueries.delete(ids.toList())

    fun markLocalMediaSyncedBefore(synced: Boolean) = preferences.set(keyLocalSyncedBefore, synced)

    fun hasLocalMediaBeenSyncedBefore(): Boolean =
        // true by default for users that had the app before this was introduced
        // will be set to false during Welcome
        preferences.get(keyLocalSyncedBefore, true)

    private suspend fun <T : LocalMediaStoreServiceItem> List<T>.processAndInsertItems(
        bucketId: Int? = null,
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
        removeMissingItems: Boolean = true,
        forceProcess: Boolean = false,
    ) {
        onProgressChange(0, 0)
        val existingIds = when {
            forceProcess -> emptySet()
            else -> if (bucketId != null) {
                localMediaItemDetailsQueries.getExistingBucketIds(bucketId)
            } else {
                localMediaItemDetailsQueries.getExistingIds()
            }.awaitList().toSet()
        }
        if (removeMissingItems) {
            val currentIds = map { it.id }.toSet()
            async {
                for (id in existingIds - currentIds) {
                    removeItemsFromDb(id)
                }
            }
        }
        val newItems = filter { it.id !in existingIds }
        for ((index, item) in newItems.withIndex<LocalMediaStoreServiceItem>()) {
            onProgressChange(index, newItems.size)
            async {
                localMediaProcessor.processNewItem(item) { itemDetails: LocalMediaItemDetails ->
                    database.transaction {
                        downloadingMediaItemsQueries.removeStartingWith(itemDetails.md5)
                        localMediaItemDetailsQueries.insert(itemDetails)
                    }
                }
            }
        }
    }
}
