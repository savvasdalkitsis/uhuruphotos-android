/*
Copyright 2024 Savvas Dalkitsis

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
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.model.uploadId
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.http.UploadService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ChunkedUploader @Inject constructor(
    private val uploadRepository: UploadRepository,
    private val contentResolver: ContentResolver,
    private val uploadService: UploadService,
) {
    private val maxChunkSize = 1_000_000

    suspend fun uploadInChunks(
        item: UploadItem,
        size: Int,
        user: User,
        progress: suspend (current: Long, total: Long) -> Unit,
    ): SimpleResult = runCatchingWithLog {
        val initialOffset = uploadRepository.getOffset(item.id) ?: 0
        contentResolver.openInputStream(Uri.parse(item.contentUri))!!.use { input ->
            val total = (size.takeIf { it > 0 } ?: input.available()).toLong()
            input.skip(initialOffset)
            val chunk = ByteArray(maxChunkSize)
            var currentOffset = initialOffset

            progress(currentOffset, total)
            var chunkSize: Int
            do {
                chunkSize = input.read(chunk)
                val uploadId = with(uploadRepository) {
                    item.uploadId()
                }
                if (chunkSize > 0) {
                    val result = uploadService.uploadChunk(
                        range = "bytes $currentOffset-${currentOffset + chunkSize - 1}/$chunkSize",
                        uploadId = MultipartBody.Part.createFormData("upload_id", uploadId),
                        user = MultipartBody.Part.createFormData("user", user.id.toString()),
                        offset = MultipartBody.Part.createFormData(
                            "offset",
                            currentOffset.toString()
                        ),
                        md5 = MultipartBody.Part.createFormData(
                            "md5",
                            ""
                        ), // see https://docs.librephotos.com/docs/development/contribution/backend/upload/
                        chunk = MultipartBody.Part.createFormData(
                            "file", "blob", chunk.toRequestBody(
                                contentType = "application/octet-stream".toMediaType(),
                                byteCount = chunkSize,
                            )
                        )
                    )
                    currentOffset += chunkSize
                    uploadRepository.updateOffset(item.id, currentOffset)
                    uploadRepository.setUploadId(item.id, result.uploadId)
                    progress(currentOffset, total)
                }
            } while (chunkSize > 0)
        }
    }
}