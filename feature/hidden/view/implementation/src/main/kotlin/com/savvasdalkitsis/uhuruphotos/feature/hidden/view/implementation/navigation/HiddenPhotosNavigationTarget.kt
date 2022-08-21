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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.api.HiddenPhotosNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAction.Load
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosEffect
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.ui.HiddenPhotosAlbumPage
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.viewmodel.HiddenPhotosViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import javax.inject.Inject

internal class HiddenPhotosNavigationTarget @Inject constructor(
    private val galleriaEffectsHandler: GalleriaEffectsHandler,
    private val hiddenPhotosEffectHandler: HiddenPhotosEffectHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<GalleriaState, HiddenPhotosState>,
                Either<GalleriaEffect, HiddenPhotosEffect>,
                Either<GalleriaAction, HiddenPhotosAction>,
                HiddenPhotosViewModel
        >(
            name = HiddenPhotosNavigationTarget.registrationName,
            effects = CompositeEffectHandler(galleriaEffectsHandler, hiddenPhotosEffectHandler),
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