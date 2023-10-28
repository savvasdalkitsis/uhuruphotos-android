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

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadId
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import kotlinx.coroutines.flow.Flow

interface UploadUseCase {

    suspend fun canUpload(): UploadCapability
    suspend fun scheduleUpload(vararg items: UploadItem)
    fun observeUploading(): Flow<Set<Long>>
    suspend fun exists(md5: Md5Hash): Result<Boolean, Throwable>
    suspend fun uploadInChunks(
        item: UploadItem,
        size: Int,
        progress: suspend (current: Long, total: Long) -> Unit,
    ): Result<UploadId, Throwable>
    fun markAsNotUploading(vararg mediaIds: Long)
}
