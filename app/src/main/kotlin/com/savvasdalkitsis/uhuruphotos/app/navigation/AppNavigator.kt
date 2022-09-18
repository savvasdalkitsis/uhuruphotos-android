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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.implementation.usecase.UiUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AppNavigator @Inject constructor(
    private val navigationTargets: Set<@JvmSuppressWildcards NavigationTarget>,
    private val navigator: Navigator,
    private val uiUseCase: UiUseCase,
    private val exoplayerProvider: ExoplayerProvider,
    private val settingsUseCase: SettingsUseCase,
) {

    @Composable
    fun NavigationTargets() {
        val navHostController = rememberAnimatedNavController()
        navigator.navController = navHostController
        with(uiUseCase) {
            keyboardController = LocalSoftwareKeyboardController.current!!
            systemUiController = LocalSystemUiController.current
            haptics = LocalHapticFeedback.current
        }
        val mapProvider by settingsUseCase.observeMapProvider().collectAsState(MapProvider.default)
        val animateVideoThumbnails = settingsUseCase.observeAnimateVideoThumbnails().collectAsState(
            initial = true
        )
        CompositionLocalProvider(
            LocalExoPlayerProvider provides exoplayerProvider,
            LocalAnimatedVideoThumbnails provides animateVideoThumbnails.value,
            LocalMapProvider provides mapProvider,
        ) {
            AnimatedNavHost(
                navController = navHostController,
                startDestination = HomeNavigationTarget.name
            ) {
                runBlocking {
                    navigationTargets.forEach { navigationTarget ->
                        with(navigationTarget) { create(navHostController) }
                    }
                }
            }
        }
    }
}