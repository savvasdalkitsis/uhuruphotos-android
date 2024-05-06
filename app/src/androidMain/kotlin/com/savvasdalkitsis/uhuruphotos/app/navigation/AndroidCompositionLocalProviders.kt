/*
Copyright 2024 Savvas Dalkitsis

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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.LocalCollageShapeProvider
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.LocalCollageSpacingEdgesProvider
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.LocalCollageSpacingProvider
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalUiController
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.UiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.LocalSharedElementTransition
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.state.SharedElementTransition
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState

class AndroidCompositionLocalProviders(
    private val navigator: Navigator,
    private val exoplayerProvider: ExoplayerProvider,
    private val settingsUIUseCase: SettingsUIUseCase,
    private val fullImageLoader: ImageLoader,
    private val thumbnailImageLoader: ImageLoader,
    private val thumbnailImageWithNetworkCacheSupportLoader: ImageLoader,
    private val serverUseCase: ServerUseCase,
    private val mapViewFactoryProvider: MapViewFactoryProvider,
    private val mapViewStateFactory: MapViewStateFactory,
) : CompositionLocalProviders {
    @Composable
    override fun Provide(
        content: @Composable () -> Unit,
    ) {
        val mapProvider by settingsUIUseCase.observeMapProvider().collectAsState(MapProvider.default)
        val animateVideoThumbnails = settingsUIUseCase.observeAnimateVideoThumbnails().collectAsState(
            initial = true
        )
        val screenshotState = rememberScreenshotState()
        val themeMode by settingsUIUseCase.observeThemeMode().collectAsState(ThemeMode.default)
        val collageShape by settingsUIUseCase.observeCollageShape().collectAsState(CollageShape.default)
        val collageSpacing by settingsUIUseCase.observeCollageSpacing().collectAsState(2)
        val collageSpacingEdges by settingsUIUseCase.observeCollageSpacingIncludeEdges().collectAsState(false)
        val sharedElementTransition = remember(screenshotState) {
            SharedElementTransition(screenshotState)
        }
        val context = LocalContext.current
        val systemUiController = rememberSystemUiController()
        val controller = remember(systemUiController) {
            UiController(context, systemUiController)
        }
        CompositionLocalProvider(
            LocalExoPlayerProvider provides exoplayerProvider,
            LocalAnimatedVideoThumbnails provides animateVideoThumbnails.value,
            LocalMapProvider provides mapProvider,
            LocalFullImageLoader provides fullImageLoader,
            LocalThumbnailImageLoader provides thumbnailImageLoader,
            LocalThumbnailWithNetworkCacheImageLoader provides thumbnailImageWithNetworkCacheSupportLoader,
            LocalNavigator provides navigator,
            LocalServerUrl provides serverUseCase.getServerUrl(),
            LocalThemeMode provides themeMode,
            LocalCollageShapeProvider provides collageShape,
            LocalCollageSpacingProvider provides collageSpacing,
            LocalCollageSpacingEdgesProvider provides collageSpacingEdges,
            LocalSharedElementTransition provides sharedElementTransition,
            LocalMapViewStateFactory provides mapViewStateFactory,
            LocalMapViewFactoryProvider provides mapViewFactoryProvider,
            LocalUiController provides controller,
        ) {
            ScreenshotBox(screenshotState = screenshotState, content = content)
        }
    }
}