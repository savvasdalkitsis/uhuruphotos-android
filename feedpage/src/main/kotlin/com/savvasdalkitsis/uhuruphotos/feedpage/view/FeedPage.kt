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
package com.savvasdalkitsis.uhuruphotos.feedpage.view

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.account.view.LogOutConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.accountoverview.view.AccountOverviewPopUp
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.AlbumSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DeleteSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DismissSelectedPhotosDeletion
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.EditServer
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.LogOut
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.PhotoLongPressed
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.RefreshAlbums
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.SettingsClick
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.UserBadgePressed
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.home.view.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.blurIf
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.photos.view.DeletePermissionDialog
import kotlinx.coroutines.launch

@Composable
fun FeedPage(
    controllersProvider: ControllersProvider,
    state: FeedPageState,
    action: (FeedPageAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun scrollToTop() {
        coroutineScope.launch {
            listState.animateScrollToItem(0, 0)
        }
    }

    HomeScaffold(
        modifier = Modifier.blurIf(state.showAccountOverview),
        title = {
            FeedPageTitle(state, action, ::scrollToTop)
        },
        navController = controllersProvider.navController!!,
        userInformationState = state.userInformationState,
        showLibrary = state.showLibrary,
        homeFeedDisplay = state.feedState.feedDisplay,
        selectionMode = state.hasSelection,
        userBadgePressed = { action(UserBadgePressed) },
        actionBarContent = {
            FeedPageActionBar(state, action)
        },
        onReselected = { scrollToTop() },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = { action(RefreshAlbums) }
        ) {
            Feed(
                contentPadding = contentPadding,
                state = state.feedState,
                showSelectionHeader = state.hasSelection,
                listState = listState,
                onPhotoSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale,))
                },
                onChangeDisplay = { action(ChangeDisplay(it as FeedDisplays)) },
                onPhotoLongPressed = {
                    action(PhotoLongPressed(it))
                }
            ) {
                action(AlbumSelectionClicked(it))
            }
        }
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userInformationState = state.userInformationState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(AskToLogOut) },
            onEditServerClicked = { action(EditServer) },
            onSettingsClicked = { action(SettingsClick) },
        )
        if (state.showPhotoDeletionConfirmationDialog) {
            DeletePermissionDialog(
                photoCount = state.selectedPhotoCount,
                onDismiss = { action(DismissSelectedPhotosDeletion) },
                onDelete = { action(DeleteSelectedPhotos) }
            )
        }
        if (state.showLogOutConfirmation) {
            LogOutConfirmationDialog(
                onDismiss = { action(DismissLogOutDialog) },
                onLogOut = { action(LogOut) },
            )
        }
    }
}