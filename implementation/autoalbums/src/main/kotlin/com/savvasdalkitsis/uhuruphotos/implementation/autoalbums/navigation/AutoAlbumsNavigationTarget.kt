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
package com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.autoalbums.navigation.AutoAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.seam.AutoAlbumsAction
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.seam.AutoAlbumsEffect
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.seam.AutoAlbumsEffectHandler
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.view.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.viewmodel.AutoAlbumsViewModel
import javax.inject.Inject

class AutoAlbumsNavigationTarget @Inject constructor(
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
    private val autoAlbumsEffectsHandler: AutoAlbumsEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<AutoAlbumsState, AutoAlbumsEffect, AutoAlbumsAction, AutoAlbumsViewModel>(
            name = AutoAlbumsNavigationTarget.name,
            effects = autoAlbumsEffectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(AutoAlbumsAction.Load) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            AutoAlbums(
                state = state,
                action = actions,
            )
        }
    }
}