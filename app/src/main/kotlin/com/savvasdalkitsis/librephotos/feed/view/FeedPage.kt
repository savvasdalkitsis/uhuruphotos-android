package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.librephotos.account.view.AccountOverviewPopUp
import com.savvasdalkitsis.librephotos.extensions.blurIf
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageAction.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider

@ExperimentalAnimationApi
@Composable
fun FeedPage(
    controllersProvider: ControllersProvider,
    state: FeedPageState,
    action: (FeedPageAction) -> Unit,
) {
    HomeScaffold(
        modifier = Modifier
            .blurIf(state.showAccountOverview),
        controllersProvider.navController!!,
        userBadgeState = state.userBadgeState,
        userBadgePressed = { action(UserBadgePressed) },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = { action(RefreshAlbums) }
        ) {
            Feed(
                contentPadding,
                state.feedState,
                onPhotoSelected = { action(SelectedPhoto(it)) },
                onChangeDisplay = { action(ChangeDisplay(it)) }
            )
        }
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userBadgeState = state.userBadgeState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(LogOut) },
        )
    }
}