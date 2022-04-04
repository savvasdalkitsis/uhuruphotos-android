package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
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
import com.savvasdalkitsis.librephotos.main.view.MainScaffold
import com.savvasdalkitsis.librephotos.navigation.BottomNavItem
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.userbadge.view.UserBadge
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

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
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.primarySurface.copy(alpha = 0.8f)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                BottomNavItem(
                    currentDestination, navController,
                    label = "Feed",
                    routeName = FeedNavigationTarget.name,
                    Icons.Filled.Home,
                )
                BottomNavItem(
                    currentDestination, navController,
                    label = "Search",
                    routeName = SearchNavigationTarget.name,
                    Icons.Filled.Search,
                )
            }
        },
        actionBarContent = {
            userBadgeState?.let {
                UserBadge(state = it, userBadgePressed = userBadgePressed)
            }
        }
    ) { contentPadding ->
        content(contentPadding)
    }
}