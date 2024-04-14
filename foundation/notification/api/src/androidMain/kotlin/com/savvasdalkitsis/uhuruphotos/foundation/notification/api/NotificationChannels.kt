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

import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

enum class NotificationChannels(
    val id: String,
    val importance: Int,
    @StringRes val label: Int,
    @StringRes val description: Int,
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
