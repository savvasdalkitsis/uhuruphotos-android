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

import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.getString
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.local_media_scan_completed
import uhuruphotos_android.foundation.strings.api.generated.resources.local_media_scan_completed_description
import uhuruphotos_android.foundation.strings.api.generated.resources.scanning_local_media

@HiltWorker
internal class LocalMediaSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val localMediaUseCase: LocalMediaUseCase,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<BroadcastReceiver>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = string.scanning_local_media,
    notificationId = NOTIFICATION_ID,
) {

    override suspend fun work(): Result {
        log { "Starting local media sync" }
        return try {
            localMediaUseCase.refreshAll { current, total ->
                delay(100)
                updateProgress(current, total)
            }
            Result.success()
        } catch (e: Exception) {
            log(e)
            Result.failure()
        }
    }

    @SuppressLint("RestrictedApi")
    override suspend fun getFinishedNotification(
        result: Result,
    ): Pair<Int, Notification>? =
        if (result is Result.Success && !localMediaUseCase.hasLocalMediaBeenSyncedBefore()) {
            localMediaUseCase.markLocalMediaSyncedBefore(true)
            FINISHED_NOTIFICATION_ID to foregroundInfoBuilder.buildNotification<BroadcastReceiver>(
                context = context,
                title = string.local_media_scan_completed,
                channel = NotificationChannels.Jobs.id,
                showProgress = false,
                autoCancel = true,
                text = getString(string.local_media_scan_completed_description),
            )
        } else {
            null
        }

    companion object {
        // string value needs to remain as is for backwards compatibility
        const val WORK_NAME = "syncMediaStore"
        private const val NOTIFICATION_ID = 1280
        private const val FINISHED_NOTIFICATION_ID = 1289
    }
}