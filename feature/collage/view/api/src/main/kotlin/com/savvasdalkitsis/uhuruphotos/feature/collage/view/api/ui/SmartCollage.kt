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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape.RECTANGLE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape.ROUNDED_RECTANGLE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.LocalScreenshotState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartGrid
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartGridItemScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartGridScrollbarThumb
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.rememberSmartGridState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
internal fun SmartCollage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: ImmutableList<Cluster>,
    showSelectionHeader: Boolean = false,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    showStickyHeaders: Boolean = false,
    showScrollbarHint: Boolean = false,
    celsSelectionMode: CelSelectionMode = CelSelectionMode.SELECTABLE,
    columnCount: Int,
    gridState: SmartGridState = rememberSmartGridState(staggered = true),
    collageHeader: @Composable() (SmartGridItemScope.() -> Unit)? = null,
    collageFooter: @Composable() (SmartGridItemScope.() -> Unit)? = null,
    onCelSelected: CelSelected,
    onCelLongPressed: (CelState) -> Unit,
    onClusterRefreshClicked: (Cluster) -> Unit,
    onClusterSelectionClicked: (Cluster) -> Unit,
) {
    Box {
        val topPadding = remember {
            contentPadding.calculateTopPadding()
        }
        val density = LocalDensity.current
        val firstOffscreenCluster by remember {
            derivedStateOf {
                with(density) {
                    gridState.layoutInfo.visibleItemsInfo
                        .firstOrNull { it.offset.y + it.size.height > topPadding.toPx() }
                        ?.key?.toString()
                        ?.takeIf { it.startsWith("item:") }
                        ?.split(":")
                        ?.get(1)
                        ?.toIntOrNull()
                }
            }
        }
        val spacing = LocalCollageSpacingProvider.current.dp
        val currentShape = LocalCollageShapeProvider.current
        val small = MaterialTheme.shapes.small
        val shape = remember(currentShape) {
            when (currentShape) {
                RECTANGLE -> RectangleShape
                ROUNDED_RECTANGLE -> small
            }
        }
        val spacingInEdges = LocalCollageSpacingEdgesProvider.current
        val horizontalExtraSpacing = remember(spacingInEdges) {
            when {
                spacingInEdges -> spacing
                else -> 0.dp
            }
        }
        val layoutDirection = LocalLayoutDirection.current
        val start = remember(layoutDirection, horizontalExtraSpacing) {
            contentPadding.calculateStartPadding(layoutDirection) + horizontalExtraSpacing
        }
        val end = remember(layoutDirection, horizontalExtraSpacing) {
            contentPadding.calculateEndPadding(layoutDirection) + horizontalExtraSpacing
        }
        SmartGrid(
            modifier = modifier
                .recomposeHighlighter()
                .padding(
                    start = start,
                    end = end,
                ),
            gridState = gridState,
            columns = columnCount,
            verticalItemSpacing = spacing,
            horizontalArrangement = spacedBy(spacing),
        ) {
            item("contentPaddingTop", "contentPadding", fullLine = true) {
                Spacer(modifier = Modifier.height(topPadding))
            }
            collageHeader?.let { header ->
                item("collageHeader", "collageHeader", fullLine = true) {
                    header(this)
                }
            }
            for ((clusterIndex, cluster) in state.withIndex()) {
                item("item:$clusterIndex:header", "header", fullLine = true) {
                    val alpha by remember {
                        derivedStateOf {
                            if (showStickyHeaders && clusterIndex == firstOffscreenCluster)
                                0f
                            else
                                1f
                        }
                    }
                    FeedClusterHeader(
                        modifier = Modifier
                            .animateItemPlacement()
                            .alpha(alpha),
                        cluster = cluster,
                        showSelectionHeader = showSelectionHeader,
                        onClusterRefreshClicked = onClusterRefreshClicked,
                        onClusterSelectionClicked = onClusterSelectionClicked,
                    )
                }

                for (cel in cluster.cels) {
                    item("item:$clusterIndex:" + cel.mediaItem.id.value) {
                        val aspectRatio = remember(maintainAspectRatio) {
                            when {
                                maintainAspectRatio -> cel.mediaItem.ratio
                                else -> 1f
                            }
                        }
                        val screenshotState = LocalScreenshotState.current
                        val scope = rememberCoroutineScope()
                        Cel(
                            modifier = Modifier
                                .animateItemPlacement()
                                .clip(shape),
                            state = cel,
                            onSelected = {
                                scope.launch {
                                    screenshotState.capture()
                                    onCelSelected(cel)
                                }
                            },
                            aspectRatio = aspectRatio,
                            contentScale = remember(maintainAspectRatio) {
                                when {
                                    maintainAspectRatio -> ContentScale.FillBounds
                                    else -> ContentScale.Crop
                                }
                            },
                            miniIcons = miniIcons,
                            selectionMode = celsSelectionMode,
                            showSyncState = showSyncState,
                            onLongClick = onCelLongPressed,
                        )
                    }
                }
            }
            collageFooter?.let { footer ->
                item("collageFooter", "collageFooter", fullLine = true) {
                    footer(this)
                }
            }
            item("contentPaddingBottom", "contentPadding", fullLine = true) {
                Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
            }
        }
        val scrollText by remember(state) {
            derivedStateOf {
                firstOffscreenCluster
                    ?.let { state[it] }
                    ?.cels
                    ?.firstOrNull()
                    ?.mediaItem
                    ?.mediaDay
                    ?.displayText
                    ?: ""
            }
        }
        if (showStickyHeaders) {
            StickyHeader(
                firstOffscreenCluster,
                topPadding,
                state,
                showSelectionHeader,
                onClusterRefreshClicked,
                onClusterSelectionClicked
            )
        }
        SmartGridScrollbarThumb(
            contentPadding,
            gridState,
            showScrollbarHint,
            scrollText
        )
    }
}

@Composable
private fun BoxScope.StickyHeader(
    firstOffscreenCluster: Int?,
    topPadding: Dp,
    state: ImmutableList<Cluster>,
    showSelectionHeader: Boolean,
    onClusterRefreshClicked: (Cluster) -> Unit,
    onClusterSelectionClicked: (Cluster) -> Unit
) {
    AnimatedVisibility(
        visible = firstOffscreenCluster != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.Companion
            .align(Alignment.TopCenter)
            .padding(top = topPadding)
            .recomposeHighlighter(),
        label = "persistent cluster",
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val cluster = remember(firstOffscreenCluster) {
            firstOffscreenCluster?.let { state[it] } ?: Cluster("")
        }
        FeedClusterHeader(
            modifier = Modifier
                .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
                .clickable(interactionSource = interactionSource, indication = null) {},
            cluster = cluster,
            showSelectionHeader = showSelectionHeader,
            onClusterRefreshClicked = onClusterRefreshClicked,
            onClusterSelectionClicked = onClusterSelectionClicked,
        )
    }
}

@Composable
private fun FeedClusterHeader(
    modifier: Modifier,
    cluster: Cluster,
    showSelectionHeader: Boolean,
    onClusterRefreshClicked: (Cluster) -> Unit,
    onClusterSelectionClicked: (Cluster) -> Unit,
) {
    val noDate = stringResource(string.no_date)
    val title = remember(cluster.displayTitle) {
        cluster.displayTitle.ifEmpty { noDate }
    }
    val location = remember(cluster.location, cluster.displayTitle) {
        cluster.location?.takeIf { cluster.displayTitle.isNotEmpty() }
    }
    ClusterHeader(
        modifier = modifier
            .recomposeHighlighter(),
        state = cluster,
        title = title,
        location = location,
        showSelectionHeader = showSelectionHeader,
        onRefreshClicked = {
            onClusterRefreshClicked(cluster)
        }
    ) {
        onClusterSelectionClicked(cluster)
    }
}