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

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import androidx.annotation.StringRes
import androidx.work.ForegroundInfo
import dev.icerock.moko.resources.StringResource

interface ForegroundInfoBuilder {
    fun build(
        context: Context,
        title: StringResource,
        notificationId: Int,
        channel: String,
        progress: Int? = null,
    ): ForegroundInfo

    fun <BR: BroadcastReceiver> buildNotification(
        context: Context,
        title: StringResource,
        channel: String,
        progress: Int? = null,
        showProgress: Boolean = true,
        autoCancel: Boolean = false,
        text: String? = null,
        cancelBroadcastReceiver: Class<BR>? = null,
    ): Notification
}