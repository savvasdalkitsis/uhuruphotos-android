package com.savvasdalkitsis.librephotos.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.librephotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.ui.window.window.LocalSystemUiController
import javax.inject.Inject

class Navigator @Inject constructor(
    private val homeNavigationTarget: HomeNavigationTarget,
    private val feedPageNavigationTarget: FeedPageNavigationTarget,
    private val searchNavigationTarget: SearchNavigationTarget,
    private val serverNavigationTarget: ServerNavigationTarget,
    private val webLoginNavigationTarget: WebLoginNavigationTarget,
    private val photoNavigationTarget: PhotoNavigationTarget,
    private val controllersProvider: ControllersProvider,
) {

    @Composable
    fun NavigationTargets(navHostController: NavHostController) {
        with(controllersProvider) {
            navController = navHostController
            keyboardController = LocalSoftwareKeyboardController.current
            focusRequester = remember { FocusRequester() }
            systemUiController = LocalSystemUiController.current
        }
        AnimatedNavHost(
            navController = navHostController,
            startDestination = HomeNavigationTarget.name
        ) {
            with(homeNavigationTarget) { create() }
            with(feedPageNavigationTarget) { create() }
            with(searchNavigationTarget) { create() }
            with(serverNavigationTarget) { create() }
            with(webLoginNavigationTarget) { create() }
            with(photoNavigationTarget) { create() }
        }
    }
}