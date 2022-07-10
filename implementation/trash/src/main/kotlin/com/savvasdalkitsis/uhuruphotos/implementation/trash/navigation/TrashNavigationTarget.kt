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
package com.savvasdalkitsis.uhuruphotos.implementation.trash.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.LoadAlbum
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.favourites.navigation.TrashNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Left
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Right
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAction
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashEffect
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.trash.view.state.TrashState
import com.savvasdalkitsis.uhuruphotos.implementation.trash.viewmodel.TrashViewModel
import javax.inject.Inject

internal class TrashNavigationTarget @Inject constructor(
    private val trashEffectsHandler: TrashEffectsHandler,
    private val albumPageEffectsHandler: AlbumPageEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<
                Pair<AlbumPageState, TrashState>,
                Either<AlbumPageEffect, TrashEffect>,
                Either<AlbumPageAction, TrashAction>,
                TrashViewModel
        >(
            name = TrashNavigationTarget.name,
            effects = CompositeEffectHandler(
                albumPageEffectsHandler,
                trashEffectsHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, action ->
                action(Left(LoadAlbum(0)))
                action(Right(Load))
            },
            createModel = { hiltViewModel() }
        ) { state, action ->
            TrashAlbumPage(state, action)
        }
}