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
package com.savvasdalkitsis.uhuruphotos.notification

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.savvasdalkitsis.uhuruphotos.icons.R

interface ForegroundInfoBuilder {
    fun build(
        context: Context,
        @StringRes title: Int,
        notificationId: Int,
        channel: String
    ): ForegroundInfo
}

fun foregroundInfo(
    context: Context,
    @StringRes title: Int,
    notificationId: Int,
    channel: String
): ForegroundInfo {
    val notification = NotificationCompat.Builder(context,channel)
        .setContentTitle(context.getString(title))
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
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