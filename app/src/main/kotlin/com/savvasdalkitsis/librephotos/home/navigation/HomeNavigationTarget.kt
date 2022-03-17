package com.savvasdalkitsis.librephotos.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.state.HomeState
import com.savvasdalkitsis.librephotos.home.view.Home
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeEffectsHandler
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.librephotos.navigation.navigationTarget

const val homeNavigationTargetName = "home"

fun NavGraphBuilder.homeNavigationTarget(navController: NavHostController) =
    navigationTarget<HomeState, HomeAction, HomeEffect, HomeViewModel>(
        name = homeNavigationTargetName,
        effects = HomeEffectsHandler(),
        viewBuilder = { state, _ -> Home(state) },
        navController,
    )