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
package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.ProcessingMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.CurrentUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow

interface UploadUseCase {

    fun observeSingleCanUpload(): Flow<UploadCapability>
    suspend fun canUpload(): UploadCapability
    suspend fun markAsUploading(force: Boolean = false, vararg items: UploadItem)
    suspend fun scheduleUploads(
        networkType: NetworkType,
        requiresCharging: Boolean,
    )
    fun observeUploading(): Flow<Set<Long>>
    fun observeProcessing(): Flow<Set<ProcessingMediaItems>>
    suspend fun upload(
        item: UploadItem,
        progress: suspend (current: Long, total: Long) -> Unit,
    ): SimpleResult
    fun markAsNotUploading(vararg mediaIds: Long)
    fun markAsNotProcessing(mediaId: Long)
    fun saveLastResponseForProcessingItem(
        itemId: Long,
        response: String,
    )
    fun saveErrorForProcessingItem(itemId: Long, error: Throwable)
    fun setCurrentUpload(currentUpload: CurrentUpload?)
    fun observeCurrentUpload(): Flow<CurrentUpload?>
}
