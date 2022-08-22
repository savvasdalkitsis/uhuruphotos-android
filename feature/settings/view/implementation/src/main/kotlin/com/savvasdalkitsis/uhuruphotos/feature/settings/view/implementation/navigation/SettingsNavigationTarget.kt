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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.api.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction.LoadSettings
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.Settings
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.controller.SettingsViewStateController
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.viewmodel.SettingsViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import javax.inject.Inject

internal class SettingsNavigationTarget @Inject constructor(
    private val settingsEffectHandler: SettingsEffectHandler,
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
    private val settingsViewStateController: SettingsViewStateController,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<SettingsState, SettingsEffect, SettingsAction, SettingsViewModel>(
            name = SettingsNavigationTarget.registrationName,
            effects = settingsEffectHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(LoadSettings) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Settings(settingsViewStateController, state, actions)
        }
    }
}