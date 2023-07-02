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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.navigation

import androidx.compose.runtime.Composable
import com.bumble.appyx.navmodel.backstack.BackStack
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.navigation.UserAlbumsNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.ui.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.viewmodel.UserAlbumsViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetRegistry
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
class UserAlbumsNavigationTarget @Inject constructor(
    registry: NavigationTargetRegistry,
    private val settingsUseCase: SettingsUseCase,
    private val navigationTargetBuilder: NavigationTargetBuilder,
) : NavigationTarget<UserAlbumsNavigationRoute>(UserAlbumsNavigationRoute::class, registry) {

    @Composable
    override fun View(route: UserAlbumsNavigationRoute, backStack: BackStack<NavigationRoute>) = with(navigationTargetBuilder) {
        ViewModelView(
            themeMode = settingsUseCase.observeThemeModeState(),
            route = route,
            viewModel = UserAlbumsViewModel::class,
        ) { state, actions ->
            UserAlbums(
                state = state,
                action = actions,
            )
        }
    }
}