package com.savvasdalkitsis.librephotos.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.feed.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.home.navigation.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.librephotos.home.navigation.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.window.WindowSize
import com.savvasdalkitsis.librephotos.window.WindowSizeClass.COMPACT

@Composable
fun homeNavigationStyle() = when (WindowSize.LOCAL_WIDTH.current) {
    COMPACT -> BOTTOM_BAR
    else -> NAVIGATION_RAIL
}

@Composable
fun HomeNavigationBar(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    feedDisplay: FeedDisplay,
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val backgroundColor = MaterialTheme.colors.primarySurface.copy(alpha = 0.8f)

    when (homeNavigationStyle()) {
        BOTTOM_BAR -> {
            BottomNavigation(
                backgroundColor = backgroundColor
            ) {
                Items(currentDestination, navController, feedDisplay, rowScope = this)
            }
        }
        NAVIGATION_RAIL -> NavigationRail(
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
            backgroundColor = backgroundColor,
        ) {
            Items(currentDestination, navController, feedDisplay)
        }
    }
}

@Composable
private fun Items(
    currentDestination: NavDestination?,
    navController: NavHostController,
    feedDisplay: FeedDisplay,
    rowScope: RowScope? = null,
) {
    NavItem(
        currentDestination, navController,
        label = "Feed",
        routeName = FeedPageNavigationTarget.name,
        painterResource(id = feedDisplay.iconResource),
        rowScope,
    )
    NavItem(
        currentDestination, navController,
        label = "Search",
        routeName = SearchNavigationTarget.name,
        icon = rememberVectorPainter(Icons.Filled.Search),
        rowScope = rowScope,
    )
}

@Composable
private fun NavItem(
    currentDestination: NavDestination?,
    navController: NavHostController,
    label: String,
    routeName: String,
    icon: Painter,
    rowScope: RowScope? = null,
) {
    when (homeNavigationStyle()) {
        BOTTOM_BAR -> BottomNavItem(
            rowScope = rowScope!!,
            currentDestination ,
            navController,
            label,
            routeName,
            icon
        )
        NAVIGATION_RAIL -> NavRailNavItem(
            currentDestination,
            navController,
            label,
            routeName,
            icon
        )
    }

}

@Composable
private fun BottomNavItem(
    rowScope: RowScope,
    currentDestination: NavDestination?,
    navController: NavHostController,
    label: String,
    routeName: String,
    icon: Painter,
) {
    with(rowScope) {
        BottomNavigationItem(
            icon = { Icon(icon, contentDescription = label) },
            label = { Text(label) },
            selected = isSelected(currentDestination, routeName),
            onClick = selectNavigationItem(currentDestination, routeName, navController)
        )
    }
}

@Composable
private fun NavRailNavItem(
    currentDestination: NavDestination?,
    navController: NavHostController,
    label: String,
    routeName: String,
    icon: Painter,
) {
    NavigationRailItem(
        selectedContentColor = LocalContentColor.current,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = isSelected(currentDestination, routeName),
        onClick = selectNavigationItem(currentDestination, routeName, navController)
    )
}

@Composable
private fun isSelected(
    currentDestination: NavDestination?,
    routeName: String
) = currentDestination?.hierarchy?.any { it.route == routeName } == true

@Composable
private fun selectNavigationItem(
    currentDestination: NavDestination?,
    routeName: String,
    navController: NavHostController
): () -> Unit = {
    if (currentDestination?.route != routeName) {
        navController.navigate(routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}