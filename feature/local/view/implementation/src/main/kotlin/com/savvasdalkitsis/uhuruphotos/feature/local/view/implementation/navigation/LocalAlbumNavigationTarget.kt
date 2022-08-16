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
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomAction
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomState
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
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
    private val showroomEffectsHandler: ShowroomEffectsHandler,
    private val localAlbumEffectHandler: LocalAlbumEffectHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<ShowroomState, LocalAlbumState>,
                Either<ShowroomEffect, LocalAlbumEffect>,
                Either<ShowroomAction, LocalAlbumAction>,
                LocalAlbumViewModel
        >(
            name = LocalAlbumNavigationTarget.registrationName,
            effects = CompositeEffectHandler(
                showroomEffectsHandler,
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