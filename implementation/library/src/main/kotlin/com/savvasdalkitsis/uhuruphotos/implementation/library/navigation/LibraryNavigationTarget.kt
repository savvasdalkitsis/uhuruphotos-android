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
package com.savvasdalkitsis.uhuruphotos.implementation.library.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.AccountOverviewActionBar
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.AccountOverviewContent
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Left
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Right
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.Library
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.implementation.library.viewmodel.LibraryViewModel
import javax.inject.Inject

class LibraryNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val accountOverviewEffectsHandler: AccountOverviewEffectsHandler,
    private val libraryEffectsHandler: LibraryEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<
                Pair<LibraryState, AccountOverviewState>,
                Either<LibraryEffect, AccountOverviewEffect>,
                Either<LibraryAction, AccountOverviewAction>,
                LibraryViewModel
        >(
            name = HomeNavigationRoutes.library,
            effects = CompositeEffectHandler(
                libraryEffectsHandler,
                accountOverviewEffectsHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions ->
                actions(Left(Load))
                actions(Right(AccountOverviewAction.Load))
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Library(
                state = state.first,
                homeFeedDisplay = FeedDisplays.default,
                isShowingPopUp = state.second.showAccountOverview,
                action = { actions(Left(it)) },
                navHostController = navHostController,
                actionBarContent = {
                    AccountOverviewActionBar(state.second) {
                        actions(Right(it))
                    }
                }
            ) {
                AccountOverviewContent(state.second) {
                    actions(Right(it))
                }
            }
        }
    }

}