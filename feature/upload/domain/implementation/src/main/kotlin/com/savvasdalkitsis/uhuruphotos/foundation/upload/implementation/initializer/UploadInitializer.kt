/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.initializer

import android.app.Application
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.TokenRefreshOkHttpClient
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.RetryPolicyConfig
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.logger.UploadServiceLogger
import net.gotev.uploadservice.logger.UploadServiceLogger.setDelegate
import net.gotev.uploadservice.logger.UploadServiceLogger.setLogLevel
import net.gotev.uploadservice.okhttp.OkHttpStack
import okhttp3.OkHttpClient
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class UploadInitializer @Inject constructor(
    @TokenRefreshOkHttpClient private val okHttpClient: OkHttpClient,
) : ApplicationCreated{
    override fun onAppCreated(app: Application) {
        with(UploadServiceConfig) {
            initialize(app, NotificationChannels.UPLOADS_CHANNEL_ID, false)
            setLogLevel(UploadServiceLogger.LogLevel.Info)
            httpStack = OkHttpStack(okHttpClient)
            bufferSizeBytes = 1024 * 1024
            retryPolicy = RetryPolicyConfig(
                initialWaitTimeSeconds = 1,
                maxWaitTimeSeconds = 100,
                multiplier = 2,
                defaultMaxRetries = 3
            )
            setDelegate(UploadLogger())
            notificationConfigFactory = { context, uploadId ->
                UploadNotificationConfig(
                    notificationChannelId = defaultNotificationChannel!!,
                    isRingToneEnabled = true,
                    progress = UploadNotificationStatusConfig(
                        title = "progress",
                        message = "some progress message"
                    ),
                    success = UploadNotificationStatusConfig(
                        title = "success",
                        message = "some success message"
                    ),
                    error = UploadNotificationStatusConfig(
                        title = "error",
                        message = "some error message"
                    ),
                    cancelled = UploadNotificationStatusConfig(
                        title = "cancelled",
                        message = "some cancelled message"
                    )
                )
            }
        }
    }
}