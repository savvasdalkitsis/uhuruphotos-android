package com.savvasdalkitsis.uhuruphotos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.uhuruphotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalSystemUiController
import javax.inject.Inject

class Navigator @Inject constructor(
    private val homeNavigationTarget: HomeNavigationTarget,
    private val feedPageNavigationTarget: FeedPageNavigationTarget,
    private val searchNavigationTarget: SearchNavigationTarget,
    private val serverNavigationTarget: ServerNavigationTarget,
    private val webLoginNavigationTarget: WebLoginNavigationTarget,
    private val photoNavigationTarget: PhotoNavigationTarget,
    private val settingsNavigationTarget: SettingsNavigationTarget,
    private val controllersProvider: ControllersProvider,
) {

    @Composable
    fun NavigationTargets() {
        val navHostController = rememberAnimatedNavController()
        with(controllersProvider) {
            navController = navHostController
            keyboardController = LocalSoftwareKeyboardController.current
            focusRequester = remember { FocusRequester() }
            systemUiController = LocalSystemUiController.current
            haptics = LocalHapticFeedback.current
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
            with(settingsNavigationTarget) { create() }
        }
    }
}