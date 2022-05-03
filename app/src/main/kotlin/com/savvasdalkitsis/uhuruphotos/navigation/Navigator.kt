package com.savvasdalkitsis.uhuruphotos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.savvasdalkitsis.uhuruphotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalSystemUiController
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class Navigator @Inject constructor(
    private val navigationTargets: Set<@JvmSuppressWildcards NavigationTarget>,
    private val controllersProvider: ControllersProvider,
) {

    @Composable
    fun NavigationTargets() {
        val navHostController = rememberAnimatedNavController()
        with(controllersProvider) {
            navController = navHostController
            keyboardController = LocalSoftwareKeyboardController.current
            systemUiController = LocalSystemUiController.current
            haptics = LocalHapticFeedback.current
        }
        AnimatedNavHost(
            navController = navHostController,
            startDestination = HomeNavigationTarget.name
        ) {
            runBlocking {
                navigationTargets.forEach { navigationTarget ->
                    with(navigationTarget) { create() }
                }
            }
        }
    }
}