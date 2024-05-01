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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.api.module.NotificationsModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.module.BiometricsModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule

internal object HomeModule {

    val homeActionsContext get() = HomeActionsContext(
        AuthModule.authenticationUseCase,
        SettingsModule.settingsUseCase,
        BiometricsModule.biometricsUseCase,
        NavigationModule.navigator,
        NotificationsModule.notificationsUseCase,
        WelcomeModule.welcomeUseCase,
    )
}