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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.usecase

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase.Companion.EMAIL
import com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.R
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.FOLLOW_SYSTEM
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.LIGHT_MODE
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class FeedbackUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loggerSetup: FileLoggerSetup,
    private val settingsUseCase: SettingsUseCase,
    private val settingsUIUseCase: SettingsUIUseCase,
) : FeedbackUseCase {

    override fun sendFeedback() {
        L.sendFeedback(
            context = context,
            attachments = listOfNotNull(loggerSetup.getLatestLogFiles()) +
                    if (settingsUseCase.getSendDatabaseEnabled())
                        listOf(context.getDatabasePath("uhuruPhotos.db"))
                    else
                        emptyList(),
            receiver = EMAIL,
        )
    }

    override fun showLogs() {
        setDefaultNightMode(
            when (settingsUIUseCase.getThemeMode()) {
                FOLLOW_SYSTEM -> MODE_NIGHT_FOLLOW_SYSTEM
                DARK_MODE -> MODE_NIGHT_YES
                LIGHT_MODE -> MODE_NIGHT_NO
            }
        )
        L.showLog(
            context = context,
            fileLoggingSetup = loggerSetup,
            receiver = EMAIL,
            theme = R.style.AppTheme,
            title = context.getString(string.logs)
        )

    }

    override suspend fun clearLogs() {
        loggerSetup.clearLogFiles()
    }
}