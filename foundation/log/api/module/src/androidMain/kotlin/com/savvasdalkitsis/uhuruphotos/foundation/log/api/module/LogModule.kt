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
package com.savvasdalkitsis.uhuruphotos.foundation.log.api.module

import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.ui.SettingsUiModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.initializer.LogInitializer

object LogModule {

    val logInitializer: ApplicationCreated by singleInstance {
        LogInitializer(
            fileTree,
            LogExtraModule.consoleTree,
            AndroidModule.applicationContext,
            loggerSetup,
            SettingsModule.settingsUseCase,
        )
    }

    val feedbackUseCase: FeedbackUseCase
        get() = com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.FeedbackUseCase(
            AndroidModule.applicationContext,
            loggerSetup,
            SettingsModule.settingsUseCase,
            SettingsUiModule.settingsUiUseCase,
        )

    private val loggerSetup: FileLoggerSetup by singleInstance {
        FileLoggerSetup.Daily.create(
            context = AndroidModule.applicationContext,
            filesToKeep = 1,
        )
    }

    private val fileTree: ILumberjackLogger
        get() = FileLogger(loggerSetup)
}