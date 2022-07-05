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
package com.savvasdalkitsis.uhuruphotos.implementation.server.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.server.navigation.ServerNavigationTarget.auto
import com.savvasdalkitsis.uhuruphotos.api.server.navigation.ServerNavigationTarget.registrationName
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.Server
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.implementation.server.viewmodel.ServerViewModel
import javax.inject.Inject

internal class ServerNavigationTarget @Inject constructor(
    private val effectsHandler: ServerEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<ServerState, ServerEffect, ServerAction, ServerViewModel>(
            name = registrationName,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action -> action(
                when {
                    navBackStackEntry.auto -> CheckPersistedServer
                    else -> RequestServerUrlChange
                })
            },
            content = { state, actions -> Server(state, actions) },
            createModel = { hiltViewModel() }
        )
}