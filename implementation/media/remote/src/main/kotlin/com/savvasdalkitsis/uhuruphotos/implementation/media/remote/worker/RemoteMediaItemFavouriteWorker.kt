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
package com.savvasdalkitsis.uhuruphotos.implementation.media.remote.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.media.remote.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.api.notification.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.api.notification.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class RemoteMediaItemFavouriteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val remoteMediaService: com.savvasdalkitsis.uhuruphotos.implementation.media.remote.service.RemoteMediaService,
    private val remoteMediaRepository: com.savvasdalkitsis.uhuruphotos.implementation.media.remote.repository.RemoteMediaRepository,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val response = remoteMediaService.setMediaItemFavourite(
                com.savvasdalkitsis.uhuruphotos.implementation.media.remote.service.model.RemoteMediaItemFavouriteRequestServiceModel(
                    mediaHashes = listOf(params.inputData.getString(KEY_ID)!!),
                    favourite = params.inputData.getBoolean(KEY_FAVOURITE, false)
                )
            )
            if (response.status) {
                response.results.forEach { mediaItem ->
                    remoteMediaRepository.insertMediaItem(mediaItem.toDbModel())
                }
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            log(e)
            if (params.runAttemptCount < 4) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }


    override suspend fun getForegroundInfo() = foregroundInfoBuilder.build(
        applicationContext,
        string.setting_media_favourite,
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID
    )

    companion object {
        const val KEY_ID = "id"
        const val KEY_FAVOURITE = "favourite"
        // string value needs to remain as is for backwards compatibility
        fun workName(id: String) = "setPhotoFavourite/$id"
        private const val NOTIFICATION_ID = 1275
    }
}