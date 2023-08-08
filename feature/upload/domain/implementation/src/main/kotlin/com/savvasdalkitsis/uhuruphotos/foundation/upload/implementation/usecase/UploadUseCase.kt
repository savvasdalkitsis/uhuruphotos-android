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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.usecase

import android.content.ContentResolver
import android.net.Uri
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.binding.binding
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.toMediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.usecase.SiteUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability.CanUpload
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability.CannotUpload
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability.UnableToCheck
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadResult
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.UploadService
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.toRequestBody
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadUseCase @Inject constructor(
    private val siteUseCase: SiteUseCase,
    private val userUseCase: UserUseCase,
    private val uploadRepository: UploadRepository,
    private val uploadService: UploadService,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val uploadWorkScheduler: UploadWorkScheduler,
    private val contentResolver: ContentResolver,
) : UploadUseCase {

    override suspend fun canUpload(): UploadCapability = siteUseCase.getSiteOptions()
        .map {
            when {
                it.allowUpload -> CanUpload
                else -> CannotUpload
            }
        }
        .getOrElse {
            log(it)
            UnableToCheck
        }

    override suspend fun scheduleUpload(vararg items: UploadItem) {
        uploadRepository.setUploading(*items)
        for (item in items) {
            uploadWorkScheduler.scheduleUploadInitialization(item)
        }
    }

    override fun markAsNotUploading(vararg mediaIds: Long) {
        uploadRepository.setNotUploading(*mediaIds)
    }

    override fun observeUploading(): Flow<Set<Long>> = uploadRepository.observeUploading()

    override suspend fun uploadChunk(
        contentUri: String,
        uploadId: String,
        offset: Long
    ): Result<UploadResult, Throwable> = try {
        binding {
            val maxChunkSize = 1_000_000
            val user = userUseCase.getUserOrRefresh().bind()
            contentResolver.openInputStream(Uri.parse(contentUri))!!.use { input ->
                input.skip(offset)
                val chunk = ByteArray(maxChunkSize)
                val chunkSize = input.read(chunk)
                val ignore = ByteArray(maxChunkSize)
                var remaining = 0L
                var read: Int
                do {
                    read = input.read(ignore)
                    if (read >= 0) {
                        remaining += read
                    }
                }
                while (read != -1)
                val result = uploadService.uploadChunk(
                    range = "bytes $offset-${offset + chunkSize - 1}/$chunkSize",
                    uploadId = createFormData("upload_id", uploadId),
                    user = createFormData("user", user.id.toString()),
                    offset = createFormData("offset", offset.toString()),
                    md5 = createFormData("md5", ""), // see https://docs.librephotos.com/docs/development/contribution/backend/upload/
                    chunk = createFormData("file", "blob", chunk.toRequestBody(
                        contentType = "application/octet-stream".toMediaType(),
                        byteCount = chunkSize,
                    ))
                )
                when (remaining) {
                    0L -> UploadResult.Finished(result.uploadId)
                    else -> UploadResult.ChunkUploaded(result.uploadId, offset + chunkSize)
                }
            }
        }
    } catch (e: Exception) {
        log(e)
        Err(e)
    }

    override suspend fun exists(md5: Md5Hash): Result<Boolean, Throwable> =
        userUseCase.getUserOrRefresh().andThen { user ->
            remoteMediaUseCase.exists(md5.toMediaItemHash(user.id))
        }
}