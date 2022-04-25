package com.savvasdalkitsis.uhuruphotos.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.home.module.HomeModule
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.view.Home
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.home.viewmodel.HomeEffectsHandler
import com.savvasdalkitsis.uhuruphotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import javax.inject.Inject

class HomeNavigationTarget @Inject constructor(
    private val effectsHandler: HomeEffectsHandler,
    private val controllersProvider: ControllersProvider,
    @HomeModule.HomeNavigationTargetFeed private val feedNavigationName: String,
    @HomeModule.HomeNavigationTargetSearch private val searchNavigationName: String,
) {

    fun NavGraphBuilder.create() =
        navigationTarget<HomeState, HomeEffect, HomeAction, HomeViewModel>(
            name = name,
            effects = effectsHandler,
            initializer = { _, actions -> actions(HomeAction.Load) },
            createModel = { hiltViewModel() }
        ) { state, _ ->
            Home(state, feedNavigationName, searchNavigationName, controllersProvider)
        }

    companion object {
        const val name = "home"
    }
}
