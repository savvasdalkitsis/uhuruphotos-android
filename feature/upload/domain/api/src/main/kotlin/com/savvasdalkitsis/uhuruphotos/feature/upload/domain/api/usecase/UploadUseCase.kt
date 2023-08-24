package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadResult
import kotlinx.coroutines.flow.Flow

interface UploadUseCase {

    suspend fun canUpload(): UploadCapability
    suspend fun scheduleUpload(vararg items: UploadItem)
    fun observeUploading(): Flow<Set<Long>>
    suspend fun exists(md5: Md5Hash): Result<Boolean, Throwable>
    suspend fun uploadChunk(
        contentUri: String,
        uploadId: String,
        offset: Long
    ): Result<UploadResult, Throwable>

    fun markAsNotUploading(vararg mediaIds: Long)
}