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
package com.savvasdalkitsis.uhuruphotos.implementation.search.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewAction.Load
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewActionBar
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewContent
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.Initialise
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.search.ui.SearchPage
import com.savvasdalkitsis.uhuruphotos.implementation.search.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.implementation.search.viewmodel.SearchViewModel
import javax.inject.Inject

class SearchNavigationTarget @Inject constructor(
    private val accountOverviewEffectsHandler: AccountOverviewEffectsHandler,
    private val searchEffectsHandler: SearchEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<
                Pair<SearchState, AccountOverviewState>,
                Either<SearchEffect, AccountOverviewEffect>,
                Either<SearchAction, AccountOverviewAction>,
                SearchViewModel
        >(
            name = HomeNavigationRoutes.search,
            effects = CompositeEffectHandler(
                searchEffectsHandler,
                accountOverviewEffectsHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions ->
                actions(Left(Initialise))
                actions(Right(Load))
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            SearchPage(
                state.first,
                isShowingPopUp = state.second.showAccountOverview,
                action = {
                    actions(Left(it))
                },
                actionBarContent = {
                    AccountOverviewActionBar(state.second) {
                        actions(Right(it))
                    }
                },
                navHostController = navHostController,
            ) {
                AccountOverviewContent(state.second) {
                    actions(Right(it))
                }
            }
        }
    }
}