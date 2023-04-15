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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewActionBar
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewContent
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.library.view.api.navigation.LibraryNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.Library
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.viewmodel.LibraryViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import javax.inject.Inject

class LibraryNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val navigationTargetBuilder: NavigationTargetBuilder,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) = with(navigationTargetBuilder) {
        navigationTarget(
            themeMode = settingsUseCase.observeThemeModeState(),
            route = LibraryNavigationRoute::class,
            viewModel = LibraryViewModel::class,
        ) { state, actions ->
            Library(
                state = state.first,
                homeFeedDisplay = PredefinedCollageDisplay.default,
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