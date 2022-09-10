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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.api.navigation.UserAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.api.navigation.UserAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.viewmodel.UserAlbumViewModel
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import javax.inject.Inject

internal class UserAlbumNavigationTarget @Inject constructor(
    private val effectsHandler: GalleryEffectsHandler,
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<GalleryState, GalleryEffect, GalleryAction, UserAlbumViewModel>(
            name = UserAlbumNavigationTarget.registrationName,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                val albumId = navBackStackEntry.albumId
                action(LoadCollage(GalleryId(albumId, "user:$albumId")))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            Gallery(
                state = state,
                action = action,
            )
        }
}