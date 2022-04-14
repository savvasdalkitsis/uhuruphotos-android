package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationBar
import com.savvasdalkitsis.librephotos.home.navigation.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.librephotos.home.navigation.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.librephotos.home.navigation.homeNavigationStyle
import com.savvasdalkitsis.librephotos.infrastructure.view.CommonScaffold
import com.savvasdalkitsis.librephotos.userbadge.view.UserBadge
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

@Composable
fun HomeScaffold(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    userBadgeState: UserBadgeState? = null,
    feedDisplay: FeedDisplay = FeedDisplay.default,
    feedNavigationName: String,
    searchNavigationName: String,
    userBadgePressed: () -> Unit = {},
    actionBarContent: @Composable RowScope.() -> Unit = {},
    onReselected: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    CommonScaffold(
        modifier = modifier,
        bottomBar = {
            if (homeNavigationStyle() == BOTTOM_BAR) {
                HomeNavigationBar(
                    navController = navController,
                    feedDisplay = feedDisplay,
                    onReselected = onReselected,
                    feedNavigationName = feedNavigationName,
                    searchNavigationName = searchNavigationName,
                )
            }
        },
        actionBarContent = {
            actionBarContent()
            userBadgeState?.let {
                UserBadge(
                    state = it,
                    userBadgePressed = userBadgePressed
                )
            }
        }
    ) { contentPadding ->
        when (homeNavigationStyle()) {
            BOTTOM_BAR -> content(contentPadding)
            NAVIGATION_RAIL -> Row {
                HomeNavigationBar(
                    contentPadding = contentPadding,
                    feedDisplay = feedDisplay,
                    navController = navController,
                    feedNavigationName = feedNavigationName,
                    searchNavigationName = searchNavigationName,
                )
                content(contentPadding)
            }
        }
    }
}