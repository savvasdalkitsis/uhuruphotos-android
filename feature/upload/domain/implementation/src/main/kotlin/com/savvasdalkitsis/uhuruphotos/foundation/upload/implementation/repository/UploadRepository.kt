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
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadIds
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadIdsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadingMediaItemsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UploadRepository @Inject constructor(
    private val uploadingMediaItemsQueries: UploadingMediaItemsQueries,
    private val uploadIdsQueries: UploadIdsQueries,
    private val database: Database,
) {

    fun setUploading(vararg mediaIds: Long) {
        database.transaction {
            mediaIds.forEach {
                uploadingMediaItemsQueries.insert(it)
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

    fun associateUploadIdWithMedia(uploadId: String, localMediaId: Long) {
        uploadIdsQueries.insert(UploadIds(uploadId, localMediaId))
    }

    fun observeUploading(): Flow<Set<Long>> = uploadingMediaItemsQueries.getAll()
        .asFlow().mapToList(Dispatchers.IO).map { it.toSet() }.distinctUntilChanged()

    suspend fun getLocalMediaIdFor(uploadId: String): Long? =
        uploadIdsQueries.get(uploadId).awaitSingleOrNull()?.localMediaId
}
