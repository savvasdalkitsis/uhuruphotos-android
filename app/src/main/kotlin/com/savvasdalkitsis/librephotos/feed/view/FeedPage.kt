package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.librephotos.account.view.AccountOverviewPopUp
import com.savvasdalkitsis.librephotos.coroutines.onMain
import com.savvasdalkitsis.librephotos.extensions.blurIf
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@Composable
fun FeedPage(
    controllersProvider: ControllersProvider,
    state: FeedPageState,
    action: (FeedAction) -> Unit,
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
            Feed(contentPadding, state.feedState)
        }
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userBadgeState = state.userBadgeState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(LogOut) },
        )
    }
}