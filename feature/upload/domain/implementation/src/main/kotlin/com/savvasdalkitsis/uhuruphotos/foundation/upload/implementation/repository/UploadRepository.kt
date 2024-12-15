/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.ProcessingMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.ProcessingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadingMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.CurrentUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UploadRepository @Inject constructor(
    private val uploadingMediaItemsQueries: UploadingMediaItemsQueries,
    private val processingMediaItemsQueries: ProcessingMediaItemsQueries,
    @PlainTextPreferences
    private val preferences: Preferences,
    private val database: Database,
    private val moshi: Moshi,
) {

    fun setUploading(vararg items: UploadItem) {
        database.transaction {
            items.forEach { item ->
                uploadingMediaItemsQueries.insert(UploadingMediaItems(
                    id = item.id,
                    contentUri = item.contentUri,
                    offset = 0L,
                    uploadId = "",
                    completed = false,
                ))
            }
        }
    }

    fun setNotUploading(vararg mediaIds: Long) {
        database.transaction {
            mediaIds.forEach {
                uploadingMediaItemsQueries.delete(it)
            }
        }
    }

    fun observeUploading(): Flow<Set<Long>> = uploadingMediaItemsQueries.getAll()
        .asFlow().mapToList(Dispatchers.IO).map { it.toSet() }.distinctUntilChanged()

    fun observeProcessing(): Flow<Set<ProcessingMediaItems>> = processingMediaItemsQueries.getAll()
        .asFlow().mapToList(Dispatchers.IO).map { it.toSet() }.distinctUntilChanged()

    suspend fun getOffset(itemId: Long): Long? =
        uploadingMediaItemsQueries.getOffset(itemId).awaitSingleOrNull()

    suspend fun isCompleted(itemId: Long): Boolean =
        uploadingMediaItemsQueries.isCompleted(itemId).awaitSingle()

    fun getUploadId(itemId: Long): String? =
        uploadingMediaItemsQueries.getUploadId(itemId).executeAsOneOrNull()

    fun setUploadId(itemId: Long, uploadId: String) {
        uploadingMediaItemsQueries.updateUploadId(uploadId, itemId)
    }

    fun updateOffset(itemId: Long, offset: Long) {
        uploadingMediaItemsQueries.updateOffset(offset, itemId)
    }

    fun setCompleted(id: Long) {
        uploadingMediaItemsQueries.setCompleted(id)
    }

    fun setProcessing(id: Long) {
        processingMediaItemsQueries.insert(id)
    }

    fun setNotProcessing(id: Long) {
        processingMediaItemsQueries.delete(id)
    }

    fun setProcessingError(id: Long, error: String) {
        processingMediaItemsQueries.setError(error, id)
    }

    fun setLastResponseForProcessing(id: Long, response: String) {
        processingMediaItemsQueries.setResponse(response, id)
    }

    private val currentUploadKey = "currentUploadKey"

    fun setCurrentlyUploading(currentUpload: CurrentUpload?) {
        if (currentUpload == null) {
            preferences.remove(currentUploadKey)
        } else {
            preferences.set(currentUploadKey, moshi.adapter(CurrentUpload::class.java).toJson(currentUpload))
        }
    }

    fun observeCurrentlyUploading(): Flow<CurrentUpload?> = preferences.observeNullableString(currentUploadKey, null).map {
        it?.let { moshi.adapter(CurrentUpload::class.java).fromJson(it) }
    }

}
