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
package com.savvasdalkitsis.uhuruphotos.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.view.Home
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.home.viewmodel.HomeEffectsHandler
import com.savvasdalkitsis.uhuruphotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.uhuruphotos.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import javax.inject.Inject

class HomeNavigationTarget @Inject constructor(
    private val effectsHandler: HomeEffectsHandler,
    private val controllersProvider: ControllersProvider,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() =
        navigationTarget<HomeState, HomeEffect, HomeAction, HomeViewModel>(
            name = HomeNavigationRoutes.home,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(HomeAction.Load) },
            createModel = { hiltViewModel() }
        ) { state, _ ->
            Home(
                state,
                controllersProvider
            )
        }
}
