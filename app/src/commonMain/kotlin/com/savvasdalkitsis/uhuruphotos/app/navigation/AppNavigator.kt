/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.navigation.integration.IntegrationPoint
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase

class AppNavigator(
    private val navigator: Navigator,
    private val uiUseCase: UiUseCase,
    private val compositionLocalProviders: CompositionLocalProviders,
) {

    @Composable
    fun NavigationTargets(
        integrationPoint: IntegrationPoint,
        lifecycle: Lifecycle,
    ) {
        compositionLocalProviders.Provide {
            val currentKeyboardController = LocalSoftwareKeyboardController.current!!
            val currentUiController = LocalUiController.current
            val currentHapticFeedback = LocalHapticFeedback.current
            LaunchedEffect(currentKeyboardController, currentUiController, currentHapticFeedback) {
                with(uiUseCase) {
                    keyboardController = currentKeyboardController
                    systemUiController = currentUiController
                    haptics = currentHapticFeedback
                }
            }
            NodeHost(
                lifecycle = lifecycle,
                integrationPoint = integrationPoint,
            ) { nodeContext ->
                val backStack: BackStack<NavigationRoute> = BackStack(
                    model = BackStackModel(
                        initialTarget = HomeNavigationRoute,
                        savedStateMap = nodeContext.savedStateMap,
                    ),
                    visualisation = {
                        NavigationRouteAwareBackStackFader(it)
                    }
                )

                navigator.backStack = backStack
                NavigationTree(nodeContext, backStack)
            }
        }
        LaunchedEffect(Unit) {
            navigator.backStack.elements.collect { stack ->
                log { "NavigationStack: " + stack.all.joinToString(">>") { it.id } }
            }
        }
    }
}