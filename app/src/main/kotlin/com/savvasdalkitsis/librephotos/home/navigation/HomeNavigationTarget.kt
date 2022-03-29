package com.savvasdalkitsis.librephotos.home.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.view.Home
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeEffectsHandler
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import javax.inject.Inject

@ExperimentalComposeUiApi
class HomeNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val imageLoader: ImageLoader,
) {

    fun NavGraphBuilder.create() =
        navigationTarget<HomeState, HomeAction, HomeEffect, HomeViewModel>(
            name = name,
            effects = HomeEffectsHandler(),
            viewBuilder = { state, _, _ ->
                Home(state, controllersProvider, imageLoader)
            },
            controllersProvider = controllersProvider,
        )

    companion object {
        const val name = "home"
    }
}
