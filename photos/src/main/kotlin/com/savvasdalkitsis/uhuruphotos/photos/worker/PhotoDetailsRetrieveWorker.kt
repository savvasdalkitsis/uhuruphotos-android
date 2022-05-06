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
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.notification.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.notification.foregroundInfo
import com.savvasdalkitsis.uhuruphotos.photos.repository.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PhotoDetailsRetrieveWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val photoRepository: PhotoRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            photoRepository.refreshDetailsNow(params.inputData.getString(KEY_ID)!!)
            Result.success()
        } catch (e: Exception) {
            log(e)
            if (params.runAttemptCount < 2) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = foregroundInfo(
        applicationContext,
        "Downloading photo details",
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID
    )

    companion object {
        const val KEY_ID = "id"
        fun workName(id: String) = "setPhotoDetailsRetrieve/$id"
        private const val NOTIFICATION_ID = 1274
    }
}