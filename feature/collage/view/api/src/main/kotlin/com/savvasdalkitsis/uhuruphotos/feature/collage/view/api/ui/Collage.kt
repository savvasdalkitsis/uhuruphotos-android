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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

import android.content.res.Configuration
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridItemScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.rememberSmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.DynamicIcon
import kotlinx.collections.immutable.ImmutableList

@Composable
fun Collage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: CollageState,
    showSelectionHeader: Boolean = false,
    showSyncState: Boolean = false,
    showStickyHeaders: Boolean = false,
    showScrollbarHint: Boolean = false,
    celsSelectionMode: CelSelectionModeState = CelSelectionModeState.SELECTABLE,
    gridState: SmartGridState = rememberSmartGridState(state.collageDisplayState.usingStaggeredGrid),
    collageHeader: @Composable (SmartGridItemScope.() -> Unit)? = null,
    collageFooter: @Composable (SmartGridItemScope.() -> Unit)? = null,
    emptyContent: @Composable () -> Unit = { NoContent(string.no_media) },
    @RawRes loadingAnimation: Int? = null,
    onCelSelected: CelSelected = { _ -> },
    onChangeDisplay: (CollageDisplayState) -> Unit = {},
    onCelLongPressed: (CelState) -> Unit = {},
    onClusterRefreshClicked: (ClusterState) -> Unit = {},
    onClusterSelectionClicked: (ClusterState) -> Unit = {},
) = when {
    isLoading(state.isLoading, state.clusterStates, state.isEmpty) -> {
        if (loadingAnimation != null) {
            FullLoading {
                DynamicIcon(icon = loadingAnimation)
            }
        } else {
            FullLoading()
        }
    }
    isEmpty(state.isLoading, state.clusterStates, state.isEmpty) -> emptyContent()
    else -> {
        val collageDisplay = state.collageDisplayState
        val animatedThumbnails = LocalAnimatedVideoThumbnails.current
        val animated = remember(animatedThumbnails, collageDisplay) {
            animatedThumbnails && collageDisplay.allowsAnimatedVideoThumbnails
        }
        CompositionLocalProvider(
            LocalAnimatedVideoThumbnails provides animated
        ) {
            val collageModifier = remember(collageDisplay) {
                modifier
                    .recomposeHighlighter()
                    .let {
                        when {
                            collageDisplay.allowsPinchGestures -> it.pinchToChange(
                                collageDisplay,
                                onChangeDisplay,
                            )

                            else -> it
                        }
                    }
            }
            SmartCollage(
                modifier = collageModifier,
                contentPadding = contentPadding,
                state = state.clusterStates,
                showSelectionHeader = showSelectionHeader,
                maintainAspectRatio = collageDisplay.maintainAspectRatio,
                miniIcons = collageDisplay.miniIcons,
                showSyncState = showSyncState,
                showStickyHeaders = showStickyHeaders,
                showScrollbarHint = showScrollbarHint,
                celsSelectionMode = celsSelectionMode,
                columnCount = collageDisplay.columnCount(
                    widthSizeClass = LocalWindowSize.current.widthSizeClass,
                    landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                ),
                gridState = gridState,
                collageHeader = collageHeader,
                collageFooter = collageFooter,
                onCelSelected = onCelSelected,
                onCelLongPressed = onCelLongPressed,
                onClusterRefreshClicked = onClusterRefreshClicked,
                onClusterSelectionClicked = onClusterSelectionClicked,
            )
        }
    }
}

@Composable
private fun isEmpty(isLoading: Boolean, clusterStates: ImmutableList<ClusterState>, isEmpty: Boolean) =
    !isLoading && isEmpty && clusterStates.isEmpty()

@Composable
private fun isLoading(isLoading: Boolean, clusterStates: ImmutableList<ClusterState>, isEmpty: Boolean) =
    (isLoading && clusterStates.isEmpty()) || (!isEmpty && clusterStates.isEmpty())

@Composable
private fun CollageDisplayState.columnCount(
    widthSizeClass: WindowWidthSizeClass,
    landscape: Boolean,
) = when {
    landscape -> when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumnsLandscape
        else -> wideColumnsLandscape
    }
    else -> when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumnsPortrait
        else -> wideColumnsPortrait
    }
}