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