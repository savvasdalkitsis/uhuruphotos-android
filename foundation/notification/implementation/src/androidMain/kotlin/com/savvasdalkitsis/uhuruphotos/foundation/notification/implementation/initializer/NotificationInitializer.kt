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
package com.savvasdalkitsis.uhuruphotos.foundation.notification.implementation.initializer

import android.app.Application
import androidx.core.app.NotificationChannelCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels

class NotificationInitializer(
    private val notificationManager: NotificationManagerCompat,
): ApplicationCreated {

    override fun priority() = -1

    override fun onAppCreated(app: Application) {
        NotificationChannels.entries.forEach { channel ->
            notificationManager.createNotificationChannel(
                Builder(channel.id, channel.importance)
                    .setName(app.getString(channel.label))
                    .setDescription(app.getString(channel.description))
                    .build()
            )
        }
    }
}