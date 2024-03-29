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
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.navmodel.backstack.BackStack
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.implementation.usecase.UiUseCase
import javax.inject.Inject

class AppNavigator @Inject constructor(
    private val navigator: Navigator,
    private val uiUseCase: UiUseCase,
    private val compositionLocalProviders: CompositionLocalProviders,
) {

    @Composable
    fun NavigationTargets(integrationPoint: ActivityIntegrationPoint) {
        with(uiUseCase) {
            keyboardController = LocalSoftwareKeyboardController.current!!
            systemUiController = LocalSystemUiController.current
            haptics = LocalHapticFeedback.current
        }
        compositionLocalProviders.Provide {
            NodeHost(
                integrationPoint = integrationPoint,
            ) { buildContext ->
                val backStack: BackStack<NavigationRoute> = BackStack(
                    initialElement = HomeNavigationRoute,
                    savedStateMap = buildContext.savedStateMap,
                )
                navigator.backStack = backStack
                NavigationTree(buildContext, backStack)
            }
        }
        LaunchedEffect(Unit) {
            navigator.backStack.elements.collect { stack ->
                log { "NavigationStack: " + stack.joinToString(">>") { it.key.navTarget.toString() } }
            }
        }
    }
}