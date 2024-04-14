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
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.logError
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
internal class LogInitializer @Inject constructor(
    private val trees: Set<@JvmSuppressWildcards ILumberjackLogger>,
    @ApplicationContext private val context: Context,
    private val fileLoggerSetup: FileLoggerSetup,
    private val settingsUseCase: SettingsUseCase,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        L.init(LumberjackLogger)
        for (tree in trees) {
            L.plant(tree)
        }
        Log.enabled = settingsUseCase.getLoggingEnabled()
        val default = Thread.getDefaultUncaughtExceptionHandler()
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
                        attachments = listOfNotNull(fileLoggerSetup.getLatestLogFiles()),
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