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
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import com.michaelflisar.lumberjack.loggers.file.FileConverter
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase.Companion.EMAIL
import com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.R
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.FOLLOW_SYSTEM
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.LIGHT_MODE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.jetbrains.compose.resources.getString
import se.ansman.dagger.auto.AutoBind
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.logs
import java.io.File
import javax.inject.Inject

@AutoBind
internal class FeedbackUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsUseCase: SettingsUseCase,
    private val settingsUIUseCase: SettingsUIUseCase,
) : FeedbackUseCase {

    private val logsFolder = File(context.filesDir, "logs")
    private val remapped = File(context.filesDir, "remapped")

    override fun sendFeedback() {
        remapped.mkdirs()
        L.sendFeedback(
            context = context,
            attachments = logsFolder.listFiles().orEmpty().toList() +
                    if (settingsUseCase.getSendDatabaseEnabled())
                        listOf(context.getDatabasePath("uhuruPhotos.db"))
                    else
                        emptyList(),
            receiver = EMAIL,
        )
    }

    override suspend fun showLogs() {
        setDefaultNightMode(
            when (settingsUIUseCase.getThemeMode()) {
                FOLLOW_SYSTEM -> MODE_NIGHT_FOLLOW_SYSTEM
                DARK_MODE -> MODE_NIGHT_YES
                LIGHT_MODE -> MODE_NIGHT_NO
            }
        )
        L.showLog(
            context = context,
            fileLoggingSetup = SharedSetup(logsFolder, remapped),
            receiver = EMAIL,
            theme = R.style.AppTheme,
            title = getString(string.logs)
        )

    }

    override suspend fun clearLogs() {
        logsFolder.deleteRecursively()
    }
}

@Parcelize
class SharedSetup(
    private val logsFolder: File,
    private val remappedFolder: File,
) : IFileLoggingSetup {

    @IgnoredOnParcel
    override val fileConverter: IFileConverter = FileConverter

    override suspend fun clearLogFiles() {
        logsFolder.deleteRecursively()
    }

    override fun getAllExistingLogFiles(): List<File> =
        logsFolder.listFiles().orEmpty().toList().map { remap(it) }

    override fun getLatestLogFiles(): File? =
        logsFolder.listFiles().orEmpty()
            .maxByOrNull { it.lastModified() }
            ?.let { remap(it) }

    private fun remap(originalFile: File): File {
        remappedFolder.mkdirs()
        val newFile = File(remappedFolder, originalFile.name)
        newFile.printWriter().use { w ->
            originalFile.readLines().forEach { line ->
                w.println(Regex("""(.*?)/(.*?): (.*)""").replace(line) {
                    "[${it.groupValues[1]}] 2000-01-01 00:00:00.000 [${it.groupValues[2]}.kt:0]: ${it.groupValues[3]}"
                })
            }
        }
        return newFile
    }
}