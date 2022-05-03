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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.account.view.LogOutConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.accountoverview.view.AccountOverviewPopUp
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.*
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.home.view.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.blurIf
import com.savvasdalkitsis.uhuruphotos.photos.view.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.ui.view.Logo
import kotlinx.coroutines.launch

@Composable
fun FeedPage(
    controllersProvider: com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider,
    state: FeedPageState,
    feedNavigationName: String,
    searchNavigationName: String,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Logo(
                    onClick = { scrollToTop() }
                )
                AnimatedVisibility(visible = state.hasSelection) {
                    OutlinedButton(
                        modifier = Modifier
                            .heightIn(max = 48.dp),
                        contentPadding = PaddingValues(2.dp),
                        onClick = { action(ClearSelected) },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = state.selectedPhotoCount.toString(),
                        )
                        ActionIcon(
                            modifier = Modifier.size(16.dp),
                            onClick = { action(ClearSelected) },
                            icon = R.drawable.ic_clear
                        )
                    }
                }
            }
        },
        selectionMode = state.hasSelection,
        navController = controllersProvider.navController!!,
        userInformationState = state.userInformationState,
        feedDisplay = state.feedState.feedDisplay,
        feedNavigationName = feedNavigationName,
        searchNavigationName = searchNavigationName,
        userBadgePressed = { action(UserBadgePressed) },
        actionBarContent = {
            AnimatedVisibility(visible = state.shouldShowShareIcon) {
                ActionIcon(onClick = { action(ShareSelectedPhotos) }, icon = R.drawable.ic_share)
            }
            AnimatedVisibility(visible = state.hasSelection) {
                ActionIcon(onClick = { action(AskForSelectedPhotosDeletion) }, icon = R.drawable.ic_delete)
            }
            FeedDisplayActionButton(
                onShow = { action(ShowFeedDisplayChoice) },
                onHide = { action(HideFeedDisplayChoice) },
                onChange = { action(ChangeDisplay(it as FeedDisplays)) },
                expanded = state.showFeedDisplayChoice,
                currentFeedDisplay = state.feedState.feedDisplay
            )
        },
        onReselected = {
            scrollToTop()
        },
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
            onSettingsClicked = { action(SettingsClick) }
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