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
package com.savvasdalkitsis.uhuruphotos.foundation.notification.api

import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN
import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.background_jobs
import uhuruphotos_android.foundation.strings.api.generated.resources.crash_reports
import uhuruphotos_android.foundation.strings.api.generated.resources.jobs
import uhuruphotos_android.foundation.strings.api.generated.resources.send_crash_logs
import uhuruphotos_android.foundation.strings.api.generated.resources.uploads

enum class NotificationChannels(
    val id: String,
    val importance: Int,
    val label: StringResource,
    val description: StringResource,
) {
    Jobs(
        id = "UHURU_PHOTOS_BACKGROUND_JOBS_CHANNEL_ID",
        importance = IMPORTANCE_MIN,
        label = string.jobs,
        description = string.background_jobs,
    ),
    Crash(
        id = "UHURU_PHOTOS_CRASH_CHANNEL_ID",
        importance = IMPORTANCE_LOW,
        label = string.crash_reports,
        description = string.send_crash_logs,
    ),
    Uploads(
        id = "UHURU_PHOTOS_UPLOADS_CHANNEL_ID",
        importance = IMPORTANCE_MIN,
        label = string.uploads,
        description = string.uploads,
    );
}
