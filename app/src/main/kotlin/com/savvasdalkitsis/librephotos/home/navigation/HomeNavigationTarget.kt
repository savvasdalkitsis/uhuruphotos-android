package com.savvasdalkitsis.librephotos.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.state.HomeState
import com.savvasdalkitsis.librephotos.home.view.Home
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeEffectsHandler
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.librephotos.navigation.NavControllerProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import javax.inject.Inject

class HomeNavigationTarget @Inject constructor(
    private val navControllerProvider: NavControllerProvider,
    private val imageLoader: ImageLoader,
) {

    fun NavGraphBuilder.create() =
        navigationTarget<HomeState, HomeAction, HomeEffect, HomeViewModel>(
            name = name,
            effects = HomeEffectsHandler(),
            viewBuilder = { state, _ -> Home(state, imageLoader) },
            navController = navControllerProvider.navController!!,
        )

    companion object {
        const val name = "home"
    }
}
