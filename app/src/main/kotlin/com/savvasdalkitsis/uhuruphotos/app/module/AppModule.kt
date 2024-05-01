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
package com.savvasdalkitsis.uhuruphotos.app.module

import com.savvasdalkitsis.uhuruphotos.app.navigation.AppNavigator
import com.savvasdalkitsis.uhuruphotos.app.navigation.CompositionLocalProviders
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.ui.SettingsUiModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.ImageModule
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.module.MapModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.module.UiModule
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.module.VideoModule

object AppModule {

    val appNavigator get() =
        AppNavigator(NavigationModule.navigator, UiModule.uiUseCase, compositionLocalProviders)

    private val compositionLocalProviders get() = CompositionLocalProviders(
        navigator = NavigationModule.navigator,
        exoplayerProvider = VideoModule.exoPlayerProvider,
        settingsUIUseCase = SettingsUiModule.settingsUiUseCase,
        fullImageLoader = ImageModule.fullImageLoader,
        thumbnailImageLoader = ImageModule.thumbnailImageLoader,
        thumbnailImageWithNetworkCacheSupportLoader = ImageModule.thumbnailImageWithNetworkCacheSupportLoader,
        serverUseCase = AuthModule.serverUseCase,
        mapViewFactoryProvider = MapModule.mapViewFactoryProvider,
        mapViewStateFactory = MapModule.mapViewStateFactory,
    )
}