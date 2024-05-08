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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.module.LogModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.module.ToasterModule

internal object ServerModule {

    val serverActionsContext get() = ServerActionsContext(
        PlatformAuthModule.serverUseCase,
        PlatformAuthModule.authenticationLoginUseCase,
        SettingsModule.settingsUseCase,
        LogModule.feedbackUseCase,
        NavigationModule.navigator,
        ToasterModule.toasterUseCase,
    )
}