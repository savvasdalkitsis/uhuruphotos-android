package com.savvasdalkitsis.librephotos.navigation

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.weblogin.navigation.WebLoginNavigationTarget
import javax.inject.Inject

class LibrePhotosNavigator @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val homeNavigationTarget: HomeNavigationTarget,
    private val serverNavigationTarget: ServerNavigationTarget,
    private val webLoginNavigationTarget: WebLoginNavigationTarget,
    private val searchNavigationTarget: SearchNavigationTarget,
) {

    @ExperimentalComposeUiApi
    @Composable
    fun NavigationTargets(
        navigationController: NavHostController,
        activity: Activity,
    ) {
        with(controllersProvider) {
            navController = navigationController
            keyboardController = LocalSoftwareKeyboardController.current
            focusRequester = remember { FocusRequester() }
        }

        NavHost(navController = navigationController, startDestination = HomeNavigationTarget.name) {
            with(homeNavigationTarget) { create() }
            with(searchNavigationTarget) { create() }
            with(serverNavigationTarget) { create(activity) }
            with(webLoginNavigationTarget) { create() }
        }
    }
}
