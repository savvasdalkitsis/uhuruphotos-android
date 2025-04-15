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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.initializer

import android.app.Application
import android.content.Context
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.notification.NotificationClickHandler
import com.michaelflisar.lumberjack.extensions.notification.showNotification
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.logError
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import dagger.hilt.android.qualifiers.ApplicationContext
import saschpe.log4k.ConsoleLogger
import saschpe.log4k.FileLogger
import saschpe.log4k.FileLogger.Limit.Files
import saschpe.log4k.FileLogger.Rotate.After
import se.ansman.dagger.auto.AutoBindIntoSet
import java.io.File
import javax.inject.Inject

@AutoBindIntoSet
internal class LogInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsUseCase: SettingsUseCase,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        Log.enabled = settingsUseCase.getLoggingEnabled()
        val default = Thread.getDefaultUncaughtExceptionHandler()

        val logsFolder = File(app.filesDir, "logs")
        saschpe.log4k.Log.loggers += FileLogger(
            rotate = After(lines = 1000),
            limit = Files(1),
            logPath = logsFolder.absolutePath,
        )
        saschpe.log4k.Log.loggers += ConsoleLogger()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            logError(e)
            try {
                L.showNotification(
                    context = context,
                    notificationIcon = R.mipmap.ic_launcher,
                    notificationChannelId = NotificationChannels.Crash.id,
                    notificationId = 1234,
                    clickHandler = NotificationClickHandler.SendFeedback(
                        context = context,
                        receiver = FeedbackUseCase.EMAIL,
                        attachments = logsFolder.listFiles().orEmpty().toList(),
                    ),
                )
                default?.uncaughtException(t, e)
            } catch (n: Throwable) {
                n.addSuppressed(e)
                default?.uncaughtException(t, n)
            }
        }
    }
}