package com.savvasdalkitsis.librephotos.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.home.module.HomeModule
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.view.Home
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeEffectsHandler
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
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
