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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeActionsContext
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule

internal object WelcomeModule {

    val welcomeActionsContext get() = WelcomeActionsContext(
        WelcomeModule.welcomeUseCase,
        PlatformAuthModule.authenticationLoginUseCase,
        NavigationModule.navigator,
        LocalMediaModule.localMediaUseCase,
    )
}