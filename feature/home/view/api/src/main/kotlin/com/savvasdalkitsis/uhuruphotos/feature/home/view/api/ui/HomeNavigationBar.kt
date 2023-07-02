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
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import com.commandiron.bubble_navigation_bar_compose.BubbleNavigationBar
import com.commandiron.bubble_navigation_bar_compose.BubbleNavigationBarItem
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.uhuruphotos.feature.library.view.api.navigation.LibraryNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.search.view.api.navigation.SearchNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalWindowSize

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
    backStack: BackStack<NavigationRoute>,
    onReselected: () -> Unit = {},
) {
    val activeRoute = backStack.activeElement

    when (homeNavigationStyle()) {
        BOTTOM_BAR -> {
            BubbleNavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
            ) {
                Items(
                    activeRoute = activeRoute,
                    backStack = backStack,
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
                activeRoute = activeRoute,
                backStack = backStack,
                homeFeedDisplay = homeFeedDisplay,
                showLibrary = showLibrary,
                onReselected = onReselected,
            )
        }
    }
}

@Composable
private fun Items(
    activeRoute: NavigationRoute?,
    backStack: BackStack<NavigationRoute>,
    homeFeedDisplay: CollageDisplay,
    showLibrary: Boolean,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    NavItem(
        activeRoute,
        backStack,
        label = string.feed,
        route = FeedNavigationRoute,
        painterResource(id = homeFeedDisplay.iconResource),
        onReselected,
        rowScope,
    )
    NavItem(
        activeRoute,
        backStack,
        label = string.search,
        route = SearchNavigationRoute,
        icon = rememberVectorPainter(Icons.Filled.Search),
        rowScope = rowScope,
        onReselected = onReselected,
    )
    if (showLibrary) {
        NavItem(
            activeRoute,
            backStack,
            label = string.library,
            route = LibraryNavigationRoute,
            icon = painterResource(drawable.ic_photo_album),
            rowScope = rowScope,
            onReselected = onReselected,
        )
    }
}

@Composable
private fun <R: NavigationRoute> NavItem(
    activeRoute: NavigationRoute?,
    backStack: BackStack<NavigationRoute>,
    @StringRes label: Int,
    route: R,
    icon: Painter,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    when (homeNavigationStyle()) {
        BOTTOM_BAR -> BottomNavItem(
            rowScope = rowScope!!,
            activeRoute,
            backStack,
            label,
            route,
            icon,
            onReselected,
        )
        NAVIGATION_RAIL -> NavRailNavItem(
            activeRoute,
            backStack,
            label,
            route,
            icon,
            onReselected,
        )
    }

}

@Composable
private fun <R: NavigationRoute> BottomNavItem(
    rowScope: RowScope,
    activeRoute: NavigationRoute?,
    backStack: BackStack<NavigationRoute>,
    @StringRes label: Int,
    route: R,
    icon: Painter,
    onReselected: () -> Unit,
) {
    with(rowScope) {
        BubbleNavigationBarItem(
            title = stringResource(label),
            iconPainter = icon,
            selectedColor = MaterialTheme.colors.primary,
            unSelectedBackgroundColor = MaterialTheme.colors.background,
            unSelectedIconColor = CustomColors.emptyItem,
            selected = activeRoute == route,
            onClick = selectNavigationItem(
                activeRoute,
                route,
                backStack,
                onReselected
            )
        )
    }
}

@Composable
private fun <R: NavigationRoute> NavRailNavItem(
    activeRoute: NavigationRoute?,
    backStack: BackStack<NavigationRoute>,
    @StringRes label: Int,
    route: R,
    icon: Painter,
    onReselected: () -> Unit,
) {
    NavigationRailItem(
        selectedContentColor = LocalContentColor.current,
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(stringResource(label)) },
        selected = activeRoute == route,
        onClick = selectNavigationItem(activeRoute, route, backStack, onReselected)
    )
}

private fun <R: NavigationRoute> selectNavigationItem(
    activeRoute: NavigationRoute?,
    route: R,
    backStack: BackStack<NavigationRoute>,
    onReselected: () -> Unit,
): () -> Unit = {
    if (activeRoute != route) {
        backStack.singleTop(FeedNavigationRoute)
        backStack.push(route)
    } else {
        onReselected()
    }
}