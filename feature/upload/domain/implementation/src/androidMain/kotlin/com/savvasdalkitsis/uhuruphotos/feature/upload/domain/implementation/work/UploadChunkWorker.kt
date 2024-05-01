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
package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.work

import android.content.Context
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Deprecated("This class is no longer used. Left for backwards compatibility")
class UploadChunkWorker(
    context: Context,
    params: WorkerParameters,
) : ForegroundNotificationWorker<Nothing>(
    context,
    params,
    notificationTitle = string.media_sync_status_uploading,
    notificationId = NOTIFICATION_ID,
    foregroundInfoBuilder = NotificationModule.foregroundInfoBuilder,
    cancelBroadcastReceiver = null,
) {

    override suspend fun work(): Result = Result.success()

    companion object {
        private const val NOTIFICATION_ID = 1285
    }
}