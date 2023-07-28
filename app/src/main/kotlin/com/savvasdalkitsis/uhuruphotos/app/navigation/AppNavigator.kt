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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import coil.ImageLoader
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.navmodel.backstack.BackStack
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.LocalAuthenticationHeaders
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationHeadersUseCase
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.FullImage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.ThumbnailImage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.ThumbnailImageWithNetworkCacheSupport
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetRegistry
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.implementation.usecase.UiUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import javax.inject.Inject

class AppNavigator @Inject constructor(
    private val navigationRegistry: NavigationTargetRegistry,
    private val navigator: Navigator,
    private val uiUseCase: UiUseCase,
    private val exoplayerProvider: ExoplayerProvider,
    private val settingsUseCase: SettingsUseCase,
    @FullImage
    private val fullImageLoader: ImageLoader,
    @ThumbnailImage
    private val thumbnailImageLoader: ImageLoader,
    @ThumbnailImageWithNetworkCacheSupport
    private val thumbnailImageWithNetworkCacheSupportLoader: ImageLoader,
    private val authenticationHeadersUseCase: AuthenticationHeadersUseCase,
) {

    @Composable
    fun NavigationTargets(integrationPoint: ActivityIntegrationPoint) {
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
            LocalFullImageLoader provides fullImageLoader,
            LocalThumbnailImageLoader provides thumbnailImageLoader,
            LocalThumbnailWithNetworkCacheImageLoader provides thumbnailImageWithNetworkCacheSupportLoader,
            LocalAuthenticationHeaders provides authenticationHeadersUseCase,
        ) {
            NodeHost(integrationPoint = integrationPoint) { buildContext ->
                val backStack: BackStack<NavigationRoute> = BackStack(
                    initialElement = HomeNavigationRoute,
                    savedStateMap = buildContext.savedStateMap,
                )
                navigator.backStack = backStack
                NavigationTree(buildContext, navigationRegistry, backStack)
            }
        }
        LaunchedEffect(Unit) {
            navigator.backStack.elements.collect { stack ->
                log { "NavigationStack: " + stack.joinToString(">>") { it.key.navTarget.toString() } }
            }
        }
    }
}