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
package com.savvasdalkitsis.uhuruphotos.implementation.autoalbum.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.LoadAlbum
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.AlbumPage
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.autoalbum.navigation.AutoAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.autoalbum.navigation.AutoAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.autoalbum.viewmodel.AutoAlbumViewModel
import javax.inject.Inject

internal class AutoAlbumNavigationTarget @Inject constructor(
    private val effectsHandler: AlbumPageEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<AlbumPageState, AlbumPageEffect, AlbumPageAction, AutoAlbumViewModel>(
            name = AutoAlbumNavigationTarget.registrationName,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                action(LoadAlbum(navBackStackEntry.albumId))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            AlbumPage(
                state,
                action
            )
        }
}