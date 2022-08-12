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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.local.navigation.LocalAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.local.navigation.LocalAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.seam.LocalAlbumAction
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.seam.LocalAlbumEffect
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.seam.LocalAlbumEffectHandler
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.view.LocalAlbumPage
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.view.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.local.viewmodel.LocalAlbumViewModel
import javax.inject.Inject

internal class LocalAlbumNavigationTarget @Inject constructor(
    private val galleryPageEffectsHandler: GalleryPageEffectsHandler,
    private val localAlbumEffectHandler: LocalAlbumEffectHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<GalleryPageState, LocalAlbumState>,
                Either<GalleryPageEffect, LocalAlbumEffect>,
                Either<GalleryPageAction, LocalAlbumAction>,
                LocalAlbumViewModel
        >(
            name = LocalAlbumNavigationTarget.registrationName,
            effects = CompositeEffectHandler(
                galleryPageEffectsHandler,
                localAlbumEffectHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                action(Either.Right(LocalAlbumAction.Load(navBackStackEntry.albumId)))
                action(Either.Left(LoadGallery(navBackStackEntry.albumId)))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            LocalAlbumPage(state, action)
        }
}