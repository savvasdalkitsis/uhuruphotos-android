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
package com.savvasdalkitsis.uhuruphotos.implementation.localalbum.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.LoadAlbum
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.localalbum.navigation.LocalAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.localalbum.navigation.LocalAlbumNavigationTarget.albumId
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.seam.LocalAlbumAction
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.seam.LocalAlbumEffect
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.seam.LocalAlbumEffectHandler
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.view.LocalAlbumPage
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.view.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.viewmodel.LocalAlbumViewModel
import javax.inject.Inject

internal class LocalAlbumNavigationTarget @Inject constructor(
    private val albumPageEffectsHandler: AlbumPageEffectsHandler,
    private val localAlbumEffectHandler: LocalAlbumEffectHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<AlbumPageState, LocalAlbumState>,
                Either<AlbumPageEffect, LocalAlbumEffect>,
                Either<AlbumPageAction, LocalAlbumAction>,
                LocalAlbumViewModel
        >(
            name = LocalAlbumNavigationTarget.registrationName,
            effects = CompositeEffectHandler(
                albumPageEffectsHandler,
                localAlbumEffectHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, action ->
                action(Either.Right(LocalAlbumAction.Load(navBackStackEntry.albumId)))
                action(Either.Left(LoadAlbum(navBackStackEntry.albumId)))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            LocalAlbumPage(state, action)
        }
}