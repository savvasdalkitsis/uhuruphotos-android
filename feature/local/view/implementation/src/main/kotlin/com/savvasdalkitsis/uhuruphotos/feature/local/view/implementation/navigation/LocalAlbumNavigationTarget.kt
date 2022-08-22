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
package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.feature.local.view.api.navigation.LocalAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.local.view.api.navigation.LocalAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumAction
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumEffect
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.LocalAlbumPage
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.viewmodel.LocalAlbumViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import javax.inject.Inject

internal class LocalAlbumNavigationTarget @Inject constructor(
    private val galleriaEffectsHandler: GalleriaEffectsHandler,
    private val localAlbumEffectHandler: LocalAlbumEffectHandler,
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<GalleriaState, LocalAlbumState>,
                Either<GalleriaEffect, LocalAlbumEffect>,
                Either<GalleriaAction, LocalAlbumAction>,
                LocalAlbumViewModel
        >(
            name = LocalAlbumNavigationTarget.registrationName,
            effects = CompositeEffectHandler(
                galleriaEffectsHandler,
                localAlbumEffectHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                action(Either.Right(LocalAlbumAction.Load(navBackStackEntry.albumId)))
                action(Either.Left(LoadCollage(navBackStackEntry.albumId)))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            LocalAlbumPage(state, action)
        }
}