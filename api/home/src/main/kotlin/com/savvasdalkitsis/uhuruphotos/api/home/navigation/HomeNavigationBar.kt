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
package com.savvasdalkitsis.uhuruphotos.api.home.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savvasdalkitsis.uhuruphotos.api.home.navigation.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.uhuruphotos.api.home.navigation.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.api.strings.R as Strings

@Composable
fun homeNavigationStyle() = when (LocalWindowSize.current.widthSizeClass) {
    Compact -> BOTTOM_BAR
    else -> NAVIGATION_RAIL
}

@Composable
fun HomeNavigationBar(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    homeFeedDisplay: FeedDisplay,
    showLibrary: Boolean,
    navController: NavHostController,
    onReselected: () -> Unit = {},
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    when (homeNavigationStyle()) {
        BOTTOM_BAR -> {
            BottomNavigation(
                elevation = 0.dp,
                backgroundColor = Color.Transparent
            ) {
                Items(
                    currentDestination = currentDestination,
                    navController = navController,
                    homeFeedDisplay = homeFeedDisplay,
                    showLibrary = showLibrary,
                    onReselected = onReselected,
                    rowScope = this,
                )
            }
        }
        NAVIGATION_RAIL -> NavigationRail(
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
        ) {
            Items(
                currentDestination = currentDestination,
                navController = navController,
                homeFeedDisplay = homeFeedDisplay,
                showLibrary = showLibrary,
                onReselected = onReselected,
            )
        }
    }
}

@Composable
private fun Items(
    currentDestination: NavDestination?,
    navController: NavHostController,
    homeFeedDisplay: FeedDisplay,
    showLibrary: Boolean,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    NavItem(
        currentDestination, navController,
        label = Strings.string.feed,
        routeName = HomeNavigationRoutes.feed,
        painterResource(id = homeFeedDisplay.iconResource),
        onReselected,
        rowScope,
    )
    NavItem(
        currentDestination, navController,
        label = Strings.string.search,
        routeName = HomeNavigationRoutes.search,
        icon = rememberVectorPainter(Icons.Filled.Search),
        rowScope = rowScope,
        onReselected = onReselected,
    )
    if (showLibrary) {
        NavItem(
            currentDestination, navController,
            label = Strings.string.library,
            routeName = HomeNavigationRoutes.library,
            icon = painterResource(R.drawable.ic_photo_album),
            rowScope = rowScope,
            onReselected = onReselected,
        )
    }
}

@Composable
private fun NavItem(
    currentDestination: NavDestination?,
    navController: NavHostController,
    @StringRes label: Int,
    routeName: String,
    icon: Painter,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    when (homeNavigationStyle()) {
        BOTTOM_BAR -> BottomNavItem(
            rowScope = rowScope!!,
            currentDestination ,
            navController,
            label,
            routeName,
            icon,
            onReselected,
        )
        NAVIGATION_RAIL -> NavRailNavItem(
            currentDestination,
            navController,
            label,
            routeName,
            icon,
            onReselected,
        )
    }

}

@Composable
private fun BottomNavItem(
    rowScope: RowScope,
    currentDestination: NavDestination?,
    navController: NavHostController,
    @StringRes label: Int,
    routeName: String,
    icon: Painter,
    onReselected: () -> Unit,
) {
    with(rowScope) {
        BottomNavigationItem(
            icon = { Icon(icon, contentDescription = null) },
            label = { Text(stringResource(label)) },
            selected = isSelected(currentDestination, routeName),
            onClick = selectNavigationItem(currentDestination, routeName, navController, onReselected)
        )
    }
}

@Composable
private fun NavRailNavItem(
    currentDestination: NavDestination?,
    navController: NavHostController,
    @StringRes label: Int,
    routeName: String,
    icon: Painter,
    onReselected: () -> Unit,
) {
    NavigationRailItem(
        selectedContentColor = LocalContentColor.current,
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(stringResource(label)) },
        selected = isSelected(currentDestination, routeName),
        onClick = selectNavigationItem(currentDestination, routeName, navController, onReselected)
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
    navController: NavHostController,
    onReselected: () -> Unit,
): () -> Unit = {
    if (currentDestination?.route != routeName) {
        navController.navigate(routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    } else {
        onReselected()
    }
}