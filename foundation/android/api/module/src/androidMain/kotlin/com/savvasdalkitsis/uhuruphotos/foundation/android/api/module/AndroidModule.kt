/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.android.api.module

import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.location.LocationManager
import androidx.core.app.NotificationManagerCompat
import com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.request.ActivityRequestLauncher
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object AndroidModule {

    lateinit var applicationContext: Context

    val contentResolver: ContentResolver
        get() = applicationContext.contentResolver

    val clipboardManager: ClipboardManager
        get() = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val notificationManager: NotificationManagerCompat
        get() = NotificationManagerCompat.from(applicationContext)

    val currentActivityHolder: CurrentActivityHolder by singleInstance {
        AndroidModule.currentActivityHolder
    }

    val downloadManager: DownloadManager
        get() = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val locationManager: LocationManager
        get() = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val activityRequestLauncher: ActivityRequestLauncher by singleInstance {
        com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.request.ActivityRequestLauncher(
            currentActivityHolder
        )
    }
}