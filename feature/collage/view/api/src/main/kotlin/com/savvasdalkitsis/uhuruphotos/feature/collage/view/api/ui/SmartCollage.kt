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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape.RECTANGLE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape.ROUNDED_RECTANGLE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGrid
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridItemScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridScrollbarThumb
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.rememberSmartGridState
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.no_date

@Composable
internal fun SharedTransitionScope.SmartCollage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: ImmutableList<ClusterState>,
    showSelectionHeader: Boolean = false,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    showStickyHeaders: Boolean = false,
    showScrollbarHint: Boolean = false,
    celsSelectionMode: CelSelectionModeState = CelSelectionModeState.SELECTABLE,
    columnCount: Int,
    gridState: SmartGridState = rememberSmartGridState(staggered = true),
    collageHeader: @Composable (SmartGridItemScope.() -> Unit)? = null,
    collageFooter: @Composable (SmartGridItemScope.() -> Unit)? = null,
    onCelSelected: CelSelected,
    onCelLongPressed: (CelState) -> Unit,
    onClusterRefreshClicked: (ClusterState) -> Unit,
    onClusterSelectionClicked: (ClusterState) -> Unit,
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
        val small = CardDefaults.outlinedShape
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
                            .animateItem()
                            .alpha(alpha),
                        clusterState = cluster,
                        showSelectionHeader = showSelectionHeader,
                        onClusterRefreshClicked = onClusterRefreshClicked,
                        onClusterSelectionClicked = onClusterSelectionClicked,
                    )
                }
                for (cel in cluster.cels) {
                    val id = cel.mediaItem.id
                    item("item:$clusterIndex:${id.serializableId}") {
                        val aspectRatio = remember(maintainAspectRatio) {
                            when {
                                maintainAspectRatio -> cel.mediaItem.ratio
                                else -> 1f
                            }
                        }
                        Cel(
                            modifier = Modifier
                                .animateItem()
                                .clip(shape),
                            state = cel,
                            onSelected = {
                                onCelSelected(cel)
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
                    ?.let { state.getOrNull(it) }
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
    state: ImmutableList<ClusterState>,
    showSelectionHeader: Boolean,
    onClusterRefreshClicked: (ClusterState) -> Unit,
    onClusterSelectionClicked: (ClusterState) -> Unit
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
        val clusterState = remember(firstOffscreenCluster) {
            firstOffscreenCluster?.let {
                state.getOrNull(it)
            } ?: ClusterState("")
        }
        FeedClusterHeader(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
                .clickable(interactionSource = interactionSource, indication = null) {},
            clusterState = clusterState,
            showSelectionHeader = showSelectionHeader,
            onClusterRefreshClicked = onClusterRefreshClicked,
            onClusterSelectionClicked = onClusterSelectionClicked,
        )
    }
}

@Composable
private fun FeedClusterHeader(
    modifier: Modifier,
    clusterState: ClusterState,
    showSelectionHeader: Boolean,
    onClusterRefreshClicked: (ClusterState) -> Unit,
    onClusterSelectionClicked: (ClusterState) -> Unit,
) {
    val noDate = stringResource(string.no_date)
    val title = remember(clusterState.displayTitle) {
        clusterState.displayTitle.ifEmpty { noDate }
    }
    val location = remember(clusterState.location, clusterState.displayTitle) {
        clusterState.location?.takeIf { clusterState.displayTitle.isNotEmpty() }
    }
    ClusterHeader(
        modifier = modifier
            .recomposeHighlighter(),
        state = clusterState,
        title = title,
        location = location,
        showSelectionHeader = showSelectionHeader,
        onRefreshClicked = {
            onClusterRefreshClicked(clusterState)
        }
    ) {
        onClusterSelectionClicked(clusterState)
    }
}