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
package com.savvasdalkitsis.uhuruphotos.autoalbum.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.api.autoalbum.navigation.AutoAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.autoalbum.navigation.AutoAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.autoalbum.seam.AutoAlbumAction
import com.savvasdalkitsis.uhuruphotos.autoalbum.seam.AutoAlbumAction.LoadAlbum
import com.savvasdalkitsis.uhuruphotos.autoalbum.seam.AutoAlbumEffect
import com.savvasdalkitsis.uhuruphotos.autoalbum.seam.AutoAlbumEffectsHandler
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.autoalbum.viewmodel.AutoAlbumViewModel
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import javax.inject.Inject

internal class AutoAlbumNavigationTarget @Inject constructor(
    private val effectsHandler: AutoAlbumEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() =
        navigationTarget<AutoAlbumState, AutoAlbumEffect, AutoAlbumAction, AutoAlbumViewModel>(
            name = AutoAlbumNavigationTarget.registrationName,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                action(LoadAlbum(navBackStackEntry.albumId))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            AutoAlbum(
                state,
                action
            )
        }
}