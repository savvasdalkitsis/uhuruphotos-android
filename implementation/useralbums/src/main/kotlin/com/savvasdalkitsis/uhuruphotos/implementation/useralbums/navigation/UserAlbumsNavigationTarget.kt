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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbums.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.useralbums.navigation.UserAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffect
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffectHandler
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsState
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.view.UserAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.viewmodel.UserAlbumsViewModel
import javax.inject.Inject

class UserAlbumsNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val userAlbumsEffectHandler: UserAlbumsEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<UserAlbumsState, UserAlbumsEffect, UserAlbumsAction, UserAlbumsViewModel>(
            name = UserAlbumsNavigationTarget.registrationName,
            effects = userAlbumsEffectHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(UserAlbumsAction.Load) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            UserAlbums(
                state = state,
                action = actions,
            )
        }
    }
}