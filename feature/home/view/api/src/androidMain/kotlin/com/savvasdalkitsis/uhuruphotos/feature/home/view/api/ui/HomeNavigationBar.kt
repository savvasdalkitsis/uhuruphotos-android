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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.activeElement
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.interactions.model.Element
import com.commandiron.bubble_navigation_bar_compose.BubbleNavigationBar
import com.commandiron.bubble_navigation_bar_compose.BubbleNavigationBarItem
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.api.navigation.DiscoverNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.uhuruphotos.feature.library.view.api.navigation.LibraryNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalBackStack
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.singleTop
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
actual fun HomeNavigationBar(
    contentPadding: PaddingValues,
    homeFeedDisplay: CollageDisplay,
    showLibrary: Boolean,
    onReselected: () -> Unit,
) {
    when (homeNavigationStyle()) {
        BOTTOM_BAR -> {
            BubbleNavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
            ) {
                Items(
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
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Items(
                    homeFeedDisplay = homeFeedDisplay,
                    showLibrary = showLibrary,
                    onReselected = onReselected,
                )
            }
        }
    }
}

@Composable
private fun Items(
    homeFeedDisplay: CollageDisplay,
    showLibrary: Boolean,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    NavItem(
        label = strings.feed,
        route = FeedNavigationRoute,
        painterResource(homeFeedDisplay.iconResource!!),
        onReselected,
        rowScope,
    )
    NavItem(
        label = strings.discover,
        route = DiscoverNavigationRoute,
        icon = rememberVectorPainter(Icons.Filled.Search),
        onReselected = onReselected,
        rowScope = rowScope,
    )
    if (showLibrary) {
        NavItem(
            label = strings.library,
            route = LibraryNavigationRoute,
            icon = painterResource(images.ic_photo_album),
            onReselected = onReselected,
            rowScope = rowScope,
        )
    }
}

@Composable
private fun <R: NavigationRoute> NavItem(
    label: StringResource,
    route: R,
    icon: Painter,
    onReselected: () -> Unit,
    rowScope: RowScope? = null,
) {
    when (homeNavigationStyle()) {
        BOTTOM_BAR -> BottomNavItem(
            rowScope = rowScope!!,
            label,
            route,
            icon,
            onReselected,
        )
        NAVIGATION_RAIL -> NavRailNavItem(
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
    label: StringResource,
    route: R,
    icon: Painter,
    onReselected: () -> Unit,
) {
    val backStack = LocalBackStack.current
    val activeRoute by remember {
        derivedStateOf { backStack().model.activeElement }
    }
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
    label: StringResource,
    route: R,
    icon: Painter,
    onReselected: () -> Unit,
) {
    val backStack = LocalBackStack.current
    val activeRoute by remember {
        derivedStateOf { backStack().model.activeElement }
    }
    NavigationRailItem(
        selectedContentColor = LocalContentColor.current,
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(stringResource(label)) },
        selected = activeRoute == route,
        onClick = selectNavigationItem(activeRoute, route, backStack, onReselected)
    )
}

private fun <R: NavigationRoute> selectNavigationItem(
    activeRoute: Element<NavigationRoute>,
    route: R,
    backStack: () -> BackStack<NavigationRoute>,
    onReselected: () -> Unit,
): () -> Unit = { with(backStack()) {
        if (activeRoute != route) {
            singleTop(FeedNavigationRoute)
            push(route)
        } else {
            onReselected()
        }
    }
}