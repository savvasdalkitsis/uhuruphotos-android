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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.BOTTOM_BAR
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.NavigationStyle.NAVIGATION_RAIL
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold

@Composable
fun HomeScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { Logo() },
    homeFeedDisplay: CollageDisplayState = PredefinedCollageDisplayState.default,
    selectionMode: Boolean = false,
    showLibrary: Boolean = true,
    showBottomNavigationBar: Boolean = true,
    actionBarContent: @Composable (RowScope.() -> Unit) = {},
    onReselected: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    UhuruScaffold(
        modifier = modifier,
        title = title,
        bottomBarDisplayed = !selectionMode && showBottomNavigationBar,
        bottomBarContent = {
            if (homeNavigationStyle() == BOTTOM_BAR) {
                HomeNavigationBar(
                    homeFeedDisplay = homeFeedDisplay,
                    showLibrary = showLibrary,
                    onReselected = onReselected,
                )
            }
        },
        actionBarContent = {
            actionBarContent()
        }
    ) { contentPadding ->
        when (homeNavigationStyle()) {
            BOTTOM_BAR -> content(contentPadding)
            NAVIGATION_RAIL -> Row {
                AnimatedVisibility(visible = !selectionMode) {
                    HomeNavigationBar(
                        contentPadding = contentPadding,
                        homeFeedDisplay = homeFeedDisplay,
                        showLibrary = showLibrary,
                        onReselected = onReselected,
                    )
                }
                content(contentPadding)
            }
        }
    }
}