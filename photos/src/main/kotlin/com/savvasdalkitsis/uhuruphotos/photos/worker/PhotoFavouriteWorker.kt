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
package com.savvasdalkitsis.uhuruphotos.photos.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.photos.service.PhotosService
import com.savvasdalkitsis.uhuruphotos.photos.service.model.PhotoFavouriteRequest
import com.savvasdalkitsis.uhuruphotos.photos.service.model.toPhotoDetails
import com.savvasdalkitsis.uhuruphotos.photos.repository.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

@HiltWorker
class PhotoFavouriteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val photosService: PhotosService,
    private val photoRepository: PhotoRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val response = photosService.setPhotoFavourite(
                PhotoFavouriteRequest(
                    imageHashes = listOf(params.inputData.getString(KEY_ID)!!),
                    favourite = params.inputData.getBoolean(KEY_FAVOURITE, false)
                )
            )
            if (response.status) {
                response.results.forEach { photo ->
                    photoRepository.insertPhoto(photo.toPhotoDetails())
                }
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            if (params.runAttemptCount < 4) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_ID = "id"
        const val KEY_FAVOURITE = "favourite"
        fun workName(id: String) = "setPhotoFavourite/$id"
    }
}