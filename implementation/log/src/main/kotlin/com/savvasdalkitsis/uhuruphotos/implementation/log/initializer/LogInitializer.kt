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
package com.savvasdalkitsis.uhuruphotos.implementation.log.initializer

import android.app.Application
import android.content.Context
import com.michaelflisar.lumberjack.FileLoggingSetup
import com.michaelflisar.lumberjack.L
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.logError
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import com.savvasdalkitsis.uhuruphotos.implementation.log.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.log.showCrashNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

internal class LogInitializer @Inject constructor(
    private val trees: Set<@JvmSuppressWildcards Timber.Tree>,
    @ApplicationContext private val context: Context,
    private val fileLoggingSetup: FileLoggingSetup,
    private val settingsUseCase: SettingsUseCase,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        for (tree in trees) {
            L.plant(tree)
        }
        Log.enabled = settingsUseCase.getLoggingEnabled()
        val default = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            logError(e)
            try {
                showCrashNotification(
                    context = context,
                    logFile = fileLoggingSetup.getLatestLogFiles(),
                    receiver = FeedbackUseCase.EMAIL,
                    appIcon = R.mipmap.ic_launcher,
                    notificationChannelId = NotificationChannels.CRASH_CHANNEL_ID,
                    notificationId = 1234,
                )
                default?.uncaughtException(t, e)
            } catch (n: Throwable) {
                n.addSuppressed(e)
                default?.uncaughtException(t, n)
            }
        }
    }
}