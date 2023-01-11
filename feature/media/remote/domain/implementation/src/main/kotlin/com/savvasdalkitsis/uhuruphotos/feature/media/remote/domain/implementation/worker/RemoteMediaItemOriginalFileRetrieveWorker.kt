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
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.usecase.RemoteMediaPrecacher
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext

@HiltWorker
class RemoteMediaItemOriginalFileRetrieveWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val remoteMediaPrecacher: RemoteMediaPrecacher,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(RemoteMediaDownloadDispatcher) {
        val id = params.inputData.getString(KEY_ID)!!
        val video = params.inputData.getBoolean(KEY_VIDEO, false)
        val url = with(remoteMediaUseCase) {
            id.toFullSizeUrlFromId(video)
        }
        val success = remoteMediaPrecacher.precacheMedia(url, video) { progress ->
            setForegroundAsync(
                createForegroundInfo(progress)
            )
        }
        createForegroundInfo(null)
        if (success) {
            Result.success()
        } else if (params.runAttemptCount < 4) {
            Result.retry()
        } else {
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo() = createForegroundInfo(null)

    private fun createForegroundInfo(progress: Int?) = foregroundInfoBuilder.build(
        applicationContext,
        string.downloading_original_file,
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID,
        progress
    )

    companion object {
        const val KEY_ID = "id"
        const val KEY_VIDEO = "video"
        // string value needs to remain as is for backwards compatibility
        fun workName(id: String) = "photoOriginalFileRetrieve/$id"
        private const val NOTIFICATION_ID = 1279
    }
}