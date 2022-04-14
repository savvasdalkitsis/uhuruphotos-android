package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.librephotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.window.LocalSystemUiController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalComposeUiApi
class LibrePhotosNavigator @Inject constructor(
    private val homeNavigationTarget: HomeNavigationTarget,
    private val feedPageNavigationTarget: FeedPageNavigationTarget,
    private val searchNavigationTarget: SearchNavigationTarget,
    private val serverNavigationTarget: ServerNavigationTarget,
    private val webLoginNavigationTarget: WebLoginNavigationTarget,
    private val photoNavigationTarget: PhotoNavigationTarget,
    private val controllersProvider: ControllersProvider,
    private val intentLauncher: IntentLauncher,
) {

    @ExperimentalCoilApi
    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @FlowPreview
    @ExperimentalAnimationApi
    @Composable
    fun NavigationTargets(navHostController: NavHostController) {
        with(controllersProvider) {
            navController = navHostController
            keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
            focusRequester = remember { FocusRequester() }
            systemUiController = LocalSystemUiController.current
            launcher = intentLauncher
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