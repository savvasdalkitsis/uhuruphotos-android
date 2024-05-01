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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.RemoteMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaItemDeletedRequestServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaOperationResponseServiceModel
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import de.jensklingenberg.ktorfit.Response
import kotlin.LazyThreadSafetyMode.NONE

class RemoteMediaItemRestoreWorker(
    context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {

    private val remoteMediaService: RemoteMediaService by lazy(NONE) {
        RemoteMediaModule.remoteMediaService
    }
    private val remoteMediaRepository: RemoteMediaRepository by lazy(NONE) {
        RemoteMediaModule.remoteMediaRepository
    }
    private val foregroundInfoBuilder: ForegroundInfoBuilder by lazy(NONE) {
        NotificationModule.foregroundInfoBuilder
    }

    override suspend fun doWork() =
        try {
            val id = params.inputData.getString(KEY_ID)!!
            val response = remoteMediaService.setMediaItemDeleted(
                RemoteMediaItemDeletedRequestServiceModel(
                    mediaHashes = listOf(id),
                    deleted = false,
                )
            )
            if (shouldRestoreLocally(response)) {
                remoteMediaRepository.restoreMediaItemFromTrash(id)
                Result.success()
            } else {
                failOrRetry()
            }
        } catch (e: Exception) {
            log(e)
            failOrRetry()
        }

    private fun shouldRestoreLocally(response: Response<RemoteMediaOperationResponseServiceModel>) =
        response.code in 200..299 && response.body()?.status == true

    private fun failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        Result.failure()
    }


    override suspend fun getForegroundInfo() = foregroundInfoBuilder.build(
        applicationContext,
        string.restoring_media,
        NOTIFICATION_ID,
        NotificationChannels.Jobs.id
    )

    companion object {
        const val KEY_ID = "id"
        // string value needs to remain as is for backwards compatibility
        fun workName(id: String) = "restorePhoto/$id"
        private const val NOTIFICATION_ID = 1278
    }
}