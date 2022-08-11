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
package com.savvasdalkitsis.uhuruphotos.implementation.hidden.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.hidden.navigation.HiddenPhotosNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Left
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Right
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosEffect
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosEffectHandler
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.view.HiddenPhotosAlbumPage
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.viewmodel.HiddenPhotosViewModel
import javax.inject.Inject

internal class HiddenPhotosNavigationTarget @Inject constructor(
    private val galleryPageEffectsHandler: GalleryPageEffectsHandler,
    private val hiddenPhotosEffectHandler: HiddenPhotosEffectHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<GalleryPageState, HiddenPhotosState>,
                Either<GalleryPageEffect, HiddenPhotosEffect>,
                Either<GalleryPageAction, HiddenPhotosAction>,
                HiddenPhotosViewModel
        >(
            name = HiddenPhotosNavigationTarget.name,
            effects = CompositeEffectHandler(galleryPageEffectsHandler, hiddenPhotosEffectHandler),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, action ->
                action(Left(LoadGallery(0)))
                action(Right(Load))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            HiddenPhotosAlbumPage(state, action)
        }
}