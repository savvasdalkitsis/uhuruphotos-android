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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class LocalMediaSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val localMediaUseCase: LocalMediaUseCase,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = string.sync_local_media,
    notificationId = NOTIFICATION_ID,
) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            updateProgress(0)
            localMediaUseCase.refreshAll { progress ->
                updateProgress(progress)
            }
            Result.success()
        } catch (e: Exception) {
            log(e)
            Result.retry()
        }
    }

    companion object {
        // string value needs to remain as is for backwards compatibility
        const val WORK_NAME = "syncMediaStore"
        private const val NOTIFICATION_ID = 1280
    }
}