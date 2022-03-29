package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.feed.navigation.FeedNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.BottomNavItem
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.main.view.MainScaffold
import com.savvasdalkitsis.librephotos.userbadge.view.UserBadge
import com.savvasdalkitsis.librephotos.userbadge.view.state.SyncState
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

@Composable
fun HomeScaffold(
    navController: NavHostController,
    userBadgeState: UserBadgeState? = null,
    imageLoader: ImageLoader?,
    content: @Composable (PaddingValues) -> Unit,
) {
    MainScaffold(
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
                UserBadge(it, imageLoader)
            }
        }
    ) { contentPadding ->
        content(contentPadding)
    }
}