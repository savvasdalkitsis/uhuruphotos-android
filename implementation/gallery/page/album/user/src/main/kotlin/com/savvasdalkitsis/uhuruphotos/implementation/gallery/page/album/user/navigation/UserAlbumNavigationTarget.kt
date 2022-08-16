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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.user.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.album.user.navigation.UserAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.album.user.navigation.UserAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.ui.GalleryPage
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.ui.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.user.viewmodel.UserAlbumViewModel
import javax.inject.Inject

internal class UserAlbumNavigationTarget @Inject constructor(
    private val effectsHandler: GalleryPageEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<GalleryPageState, GalleryPageEffect, GalleryPageAction, UserAlbumViewModel>(
            name = UserAlbumNavigationTarget.registrationName,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                action(LoadGallery(navBackStackEntry.albumId))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            GalleryPage(
                state = state,
                action = action,
            )
        }
}