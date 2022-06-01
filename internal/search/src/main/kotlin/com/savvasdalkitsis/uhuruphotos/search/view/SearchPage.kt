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
package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.account.view.LogOutConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.accountoverview.view.AccountOverviewPopUp
import com.savvasdalkitsis.uhuruphotos.feed.view.FeedDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.home.view.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.blurIf
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.EditServer
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.LogOut
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.SettingsClick
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.UserBadgePressed
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults.Found
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState

@Composable
fun SearchPage(
    state: SearchState,
    action: (SearchAction) -> Unit,
    controllersProvider: ControllersProvider,
) {
    HomeScaffold(
        modifier = Modifier
            .blurIf(state.showAccountOverview)
            .imeNestedScroll(),
        navController = controllersProvider.navController!!,
        userInformationState = state.userInformationState,
        homeFeedDisplay = state.feedDisplay,
        showLibrary = state.showLibrary,
        userBadgePressed = { action(UserBadgePressed) },
        actionBarContent = {
            AnimatedVisibility(state.searchResults is Found) {
                FeedDisplayActionButton(
                    onChange = { action(ChangeDisplay(it)) },
                    currentFeedDisplay = state.searchDisplay
                )
            }
        }
    ) { contentPadding ->
        Search(
            state = state,
            action = action,
            contentPadding = contentPadding
        )
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userInformationState = state.userInformationState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(AskToLogOut) },
            onEditServerClicked = { action(EditServer) },
            onSettingsClicked = { action(SettingsClick) },
        )
        if (state.showLogOutConfirmation) {
            LogOutConfirmationDialog(
                onDismiss = { action(DismissLogOutDialog) },
                onLogOut = { action(LogOut) },
            )
        }
    }
}