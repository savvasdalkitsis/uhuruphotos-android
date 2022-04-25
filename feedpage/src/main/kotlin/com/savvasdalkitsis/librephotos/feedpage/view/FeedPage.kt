package com.savvasdalkitsis.librephotos.feedpage.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.librephotos.account.view.LogOutConfirmationDialog
import com.savvasdalkitsis.librephotos.accountoverview.view.AccountOverviewPopUp
import com.savvasdalkitsis.librephotos.feed.view.Feed
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageAction.*
import com.savvasdalkitsis.librephotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.infrastructure.extensions.blurIf
import com.savvasdalkitsis.librephotos.photos.view.DeletePermissionDialog
import com.savvasdalkitsis.librephotos.ui.view.ActionIcon
import kotlinx.coroutines.launch

@Composable
fun FeedPage(
    controllersProvider: com.savvasdalkitsis.librephotos.navigation.ControllersProvider,
    state: FeedPageState,
    feedNavigationName: String,
    searchNavigationName: String,
    action: (FeedPageAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    HomeScaffold(
        modifier = Modifier.blurIf(state.showAccountOverview),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "LibrePhotos")
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
                onChange = { action(ChangeDisplay(it)) },
                expanded = state.showFeedDisplayChoice,
                currentFeedDisplay = state.feedState.feedDisplay
            )
        },
        onReselected = {
            coroutineScope.launch {
                listState.animateScrollToItem(0, 0)
                gridState.animateScrollToItem(0, 0)
            }
        },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = { action(RefreshAlbums) }
        ) {
            Feed(
                contentPadding,
                state.feedState,
                showSelectionHeader = state.hasSelection,
                listState = listState,
                gridState = gridState,
                onPhotoSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale,))
                },
                onChangeDisplay = { action(ChangeDisplay(it)) },
                onPhotoLongPressed = {
                    action(PhotoLongPressed(it))
                },
                onAlbumSelectionClicked = {
                    action(AlbumSelectionClicked(it))
                }
            )
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