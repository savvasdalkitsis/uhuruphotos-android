package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.savvasdalkitsis.librephotos.feed.navigation.FeedNavigationTarget
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.weblogin.navigation.WebLoginNavigationTarget
import javax.inject.Inject

@ExperimentalComposeUiApi
class LibrePhotosNavigator @Inject constructor(
    private val homeNavigationTarget: HomeNavigationTarget,
    private val feedNavigationTarget: FeedNavigationTarget,
    private val searchNavigationTarget: SearchNavigationTarget,
    private val serverNavigationTarget: ServerNavigationTarget,
    private val webLoginNavigationTarget: WebLoginNavigationTarget,
    private val controllersProvider: ControllersProvider,
) {

    @Composable
    fun NavigationTargets(navHostController: NavHostController) {
        with(controllersProvider) {
            navController = navHostController
            keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
            focusRequester = remember { FocusRequester() }
        }
        NavHost(
            navController = navHostController,
            startDestination = HomeNavigationTarget.name
        ) {
            with(homeNavigationTarget) { create() }
            with(feedNavigationTarget) { create() }
            with(searchNavigationTarget) { create() }
            with(serverNavigationTarget) { create() }
            with(webLoginNavigationTarget) { create() }
        }
    }
}