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
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import se.ansman.dagger.auto.AutoBind
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.cancel
import javax.inject.Inject

@AutoBind
internal class ForegroundInfoBuilder @Inject constructor(
) : ForegroundInfoBuilder {

    override suspend fun build(
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

    override suspend fun <BR: BroadcastReceiver> buildNotification(
        context: Context,
        title: StringResource,
        channel: String,
        progress: Int?,
        showProgress: Boolean,
        autoCancel: Boolean,
        text: String?,
        cancelBroadcastReceiver: Class<BR>?,
    ) = NotificationCompat.Builder(context, channel)
        .setContentTitle(getString(title))
        .setContentText(text)
        .setContentIntent(PendingIntent.getActivity(
            context,
            0,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_IMMUTABLE,
        ))
        .setSmallIcon(drawable.ic_notification)
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
                addAction(drawable.ic_cancel, getString(string.cancel),
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