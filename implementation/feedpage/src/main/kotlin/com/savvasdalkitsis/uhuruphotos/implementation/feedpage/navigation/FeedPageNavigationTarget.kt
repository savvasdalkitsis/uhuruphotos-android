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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.Load
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffectsHandler
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.AccountOverviewActionBar
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.AccountOverviewContent
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.api.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Left
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Right
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction.LoadFeed
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.FeedPage
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.viewmodel.FeedPageViewModel
import javax.inject.Inject

internal class FeedPageNavigationTarget @Inject constructor(
    private val accountOverviewEffectsHandler: AccountOverviewEffectsHandler,
    private val feedPageEffectsHandler: FeedPageEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<
                Pair<FeedPageState, AccountOverviewState>,
                Either<FeedPageEffect, AccountOverviewEffect>,
                Either<FeedPageAction, AccountOverviewAction>,
                FeedPageViewModel
        >(
            name = HomeNavigationRoutes.feed,
            effects = CompositeEffectHandler(
                feedPageEffectsHandler,
                accountOverviewEffectsHandler,
            ),
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions ->
                actions(Left(LoadFeed))
                actions(Right(Load))
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            FeedPage(
                navHostController,
                state.first,
                isShowingPopUp = state.second.showAccountOverview,
                action = {
                    actions(Left(it))
                },
                actionBarContent = {
                    AnimatedVisibility(visible = !state.first.hasSelection) {
                        AccountOverviewActionBar(state.second) {
                            actions(Right(it))
                        }
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
