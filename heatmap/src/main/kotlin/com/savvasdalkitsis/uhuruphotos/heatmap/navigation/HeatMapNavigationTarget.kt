package com.savvasdalkitsis.uhuruphotos.heatmap.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.heatmap.view.HeatMap
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapEffectsHandler
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapViewModel
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import javax.inject.Inject

class HeatMapNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectsHandler: HeatMapEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() =
        navigationTarget<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapViewModel>(
            name = name,
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
