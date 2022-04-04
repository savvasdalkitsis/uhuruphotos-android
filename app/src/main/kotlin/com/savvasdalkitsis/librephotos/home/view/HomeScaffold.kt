package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savvasdalkitsis.librephotos.feed.navigation.FeedNavigationTarget
import com.savvasdalkitsis.librephotos.home.navigation.*
import com.savvasdalkitsis.librephotos.home.navigation.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.librephotos.home.navigation.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.librephotos.main.view.MainScaffold
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.userbadge.view.UserBadge
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState
import com.savvasdalkitsis.librephotos.window.WindowSize
import com.savvasdalkitsis.librephotos.window.WindowSizeClass

@Composable
fun HomeScaffold(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    userBadgeState: UserBadgeState? = null,
    userBadgePressed: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    MainScaffold(
        modifier = modifier,
        bottomBar = {
            if (homeNavigationStyle() == BOTTOM_BAR) {
                HomeNavigationBar(navController = navController)
            }
        },
        actionBarContent = {
            userBadgeState?.let {
                UserBadge(state = it, userBadgePressed = userBadgePressed)
            }
        }
    ) { contentPadding ->
        when (homeNavigationStyle()) {
            BOTTOM_BAR -> content(contentPadding)
            NAVIGATION_RAIL -> Row {
                HomeNavigationBar(
                    contentPadding = contentPadding,
                    navController = navController,
                )
                content(contentPadding)
            }
        }
    }
}