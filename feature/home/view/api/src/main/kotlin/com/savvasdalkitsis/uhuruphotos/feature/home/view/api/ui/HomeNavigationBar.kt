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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.commandiron.bubble_navigation_bar_compose.BubbleNavigationBar
import com.commandiron.bubble_navigation_bar_compose.BubbleNavigationBarItem
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.uhuruphotos.feature.library.view.api.navigation.LibraryNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.search.view.api.navigation.SearchNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigationRouteSerializerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalWindowSize
import kotlin.reflect.KClass

@Composable
fun homeNavigationStyle() = when (LocalWindowSize.current.widthSizeClass) {
    Compact -> BOTTOM_BAR
    else -> NAVIGATION_RAIL
}

@Composable
fun HomeNavigationBar(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    homeFeedDisplay: CollageDisplay,
    showLibrary: Boolean,
    navController: NavHostController,
    onReselected: () -> Unit = {},
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    when (homeNavigationStyle()) {
        BOTTOM_BAR -> {
            BubbleNavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
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
            containerColor = Color.Transparent,
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
    homeFeedDisplay: CollageDisplay,
    showLibrary: Boolean,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    NavItem(
        currentDestination, navController,
        label = string.feed,
        routeName = routeFor(FeedNavigationRoute::class),
        painterResource(id = homeFeedDisplay.iconResource),
        onReselected,
        rowScope,
    )
    NavItem(
        currentDestination, navController,
        label = string.search,
        routeName = routeFor(SearchNavigationRoute::class),
        icon = rememberVectorPainter(Icons.Filled.Search),
        rowScope = rowScope,
        onReselected = onReselected,
    )
    if (showLibrary) {
        NavItem(
            currentDestination, navController,
            label = string.library,
            routeName = routeFor(LibraryNavigationRoute::class),
            icon = painterResource(drawable.ic_photo_album),
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
    val feedRoute = routeFor(FeedNavigationRoute::class)
    with(rowScope) {
        BubbleNavigationBarItem(
            title = stringResource(label),
            iconPainter = icon,
            selectedColor = MaterialTheme.colorScheme.primary,
            unSelectedBackgroundColor = MaterialTheme.colorScheme.background,
            unSelectedIconColor = CustomColors.emptyItem,
            selected = isSelected(currentDestination, routeName),
            onClick = selectNavigationItem(currentDestination, routeName, navController, feedRoute, onReselected)
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
    val feedRoute = routeFor(FeedNavigationRoute::class)
    NavigationRailItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(stringResource(label)) },
        selected = isSelected(currentDestination, routeName),
        onClick = selectNavigationItem(currentDestination, routeName, navController, feedRoute, onReselected)
    )
}

@Composable
private fun isSelected(
    currentDestination: NavDestination?,
    routeName: String
) = currentDestination?.hierarchy?.any { it.route == routeName } == true

private fun selectNavigationItem(
    currentDestination: NavDestination?,
    routeName: String,
    navController: NavHostController,
    feedRoute: String,
    onReselected: () -> Unit,
): () -> Unit = {
    if (currentDestination?.route != routeName) {
        navController.navigate(routeName) {
            popUpTo(feedRoute) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    } else {
        onReselected()
    }
}

@Composable
private fun <T : NavigationRoute> routeFor(route: KClass<T>) =
    LocalNavigationRouteSerializerProvider.current.createRouteTemplateFor(route)
