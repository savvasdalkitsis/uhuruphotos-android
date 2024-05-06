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
package com.savvasdalkitsis.uhuruphotos.foundation.notification.implementation

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc

class ForegroundInfoBuilder : ForegroundInfoBuilder {

    override fun build(
        context: Context,
        title: StringResource,
        notificationId: Int,
        channel: String,
        progress: Int?,
    ): ForegroundInfo {
        val notification = buildNotification<BroadcastReceiver>(
            context = context,
            title = title,
            channel = channel,
            progress = progress
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                notificationId,
                notification
            )
        }
    }

    override fun <BR: BroadcastReceiver> buildNotification(
        context: Context,
        title: StringResource,
        channel: String,
        progress: Int?,
        showProgress: Boolean,
        autoCancel: Boolean,
        text: String?,
        cancelBroadcastReceiver: Class<BR>?,
    ) = NotificationCompat.Builder(context, channel)
        .setContentTitle(title.desc().toString(context))
        .setContentText(text)
        .setContentIntent(PendingIntent.getActivity(
            context,
            0,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_IMMUTABLE,
        ))
        .setSmallIcon(images.ic_notification.drawableResId)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setAutoCancel(autoCancel)
        .run {
            if (showProgress) {
                setProgress(100, progress ?: 0, progress == null)
            } else {
                this
            }
        }
        .run {
            if (cancelBroadcastReceiver != null) {
                addAction(images.ic_cancel.drawableResId, strings.cancel.desc().toString(context),
                    PendingIntent.getBroadcast(
                        context,
                        1,
                        Intent(context, cancelBroadcastReceiver),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ))
            } else {
                this
            }
        }
        .build()
}