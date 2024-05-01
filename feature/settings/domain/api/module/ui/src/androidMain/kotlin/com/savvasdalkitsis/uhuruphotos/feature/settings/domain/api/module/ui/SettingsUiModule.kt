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
package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.ui

import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule

object SettingsUiModule {

    val settingsUiUseCase: SettingsUIUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.settings.domain.implementation.usecase.SettingsUIUseCase(
            PreferencesModule.plainTextPreferences,
            WelcomeModule.welcomeUseCase,
            AndroidModule.applicationContext,
        )
}