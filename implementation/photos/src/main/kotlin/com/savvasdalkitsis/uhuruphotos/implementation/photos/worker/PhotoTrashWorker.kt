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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.notification.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.api.notification.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.implementation.photos.repository.PhotoRepository
import com.savvasdalkitsis.uhuruphotos.implementation.photos.service.PhotosService
import com.savvasdalkitsis.uhuruphotos.implementation.photos.service.model.PhotoDeletedRequest
import com.savvasdalkitsis.uhuruphotos.implementation.photos.service.model.PhotoOperationResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

@HiltWorker
class PhotoTrashWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val photosService: PhotosService,
    private val photoRepository: PhotoRepository,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val id = params.inputData.getString(KEY_ID)!!
            val response = photosService.setPhotoDeleted(
                PhotoDeletedRequest(
                    imageHashes = listOf(id),
                    deleted = true,
                )
            )
            if (shouldTrashLocally(response)) {
                photoRepository.trashPhoto(id)
                Result.success()
            } else if (shouldDeleteLocally(response)) {
                photoRepository.deletePhoto(id)
                Result.success()
            } else {
                failOrRetry()
            }
        } catch (e: Exception) {
            log(e)
            failOrRetry()
        }
    }

    private fun shouldTrashLocally(response: Response<PhotoOperationResponse>) =
        response.code() in 200..299 && response.body()?.status == true

    private fun shouldDeleteLocally(response: Response<PhotoOperationResponse>) =
        response.code() == 404

    private fun failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        Result.failure()
    }


    override suspend fun getForegroundInfo() = foregroundInfoBuilder.build(
        applicationContext,
        R.string.trashing_photo,
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID
    )

    companion object {
        const val KEY_ID = "id"
        fun trashPhotoWorkName(id: String) = "trashPhoto/$id"
        private const val NOTIFICATION_ID = 1276
    }
}