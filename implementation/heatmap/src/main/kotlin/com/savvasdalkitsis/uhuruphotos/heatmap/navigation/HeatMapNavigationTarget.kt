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
package com.savvasdalkitsis.uhuruphotos.heatmap.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.heatmap.navigation.HeatMapNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapEffectsHandler
import com.savvasdalkitsis.uhuruphotos.heatmap.view.HeatMap
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapViewModel
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import javax.inject.Inject

class HeatMapNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectsHandler: HeatMapEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapViewModel>(
            name = HeatMapNavigationTarget.name,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(HeatMapAction.Load) },
            createModel = { hiltViewModel() }
        ) { state, action ->
            HeatMap(state, action)
        }

    companion object {
        const val name = "heatMap"
    }
}
