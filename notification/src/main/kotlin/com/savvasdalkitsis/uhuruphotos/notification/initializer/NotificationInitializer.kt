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
package com.savvasdalkitsis.uhuruphotos.notification.initializer

import androidx.core.app.NotificationChannelCompat.Builder
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.notification.NotificationChannels.JOBS_CHANNEL_ID
import javax.inject.Inject

class NotificationInitializer @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
): ApplicationCreated {

    override fun onAppCreated() {
        notificationManager.createNotificationChannel(
            Builder(JOBS_CHANNEL_ID, IMPORTANCE_LOW)
                .setName("Jobs")
                .setDescription("Background jobs")
                .build()
        )
    }
}