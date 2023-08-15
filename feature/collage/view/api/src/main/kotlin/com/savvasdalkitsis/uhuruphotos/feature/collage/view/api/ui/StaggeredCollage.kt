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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.collections.immutable.ImmutableList
import my.nanihadesuka.compose.ScrollbarSelectionMode

@Composable
internal fun StaggeredCollage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: ImmutableList<Cluster>,
    showSelectionHeader: Boolean = false,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    showStickyHeaders: Boolean = false,
    showScrollbarHint: Boolean = false,
    columnCount: Int,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    collageHeader: @Composable (LazyStaggeredGridItemScope.() -> Unit)? = null,
    onCelSelected: CelSelected,
    onCelLongPressed: (CelState) -> Unit,
    onClusterRefreshClicked: (Cluster) -> Unit,
    onClusterSelectionClicked: (Cluster) -> Unit,
) {
    Box {
        val topPadding = contentPadding.calculateTopPadding()
        LazyVerticalStaggeredGrid(
            modifier = modifier
                .recomposeHighlighter()
                .padding(
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                ),
            state = gridState,
            columns = StaggeredGridCells.Fixed(columnCount),
        ) {
            item("contentPaddingTop", "contentPadding", span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(topPadding))
            }
            collageHeader?.let { header ->
                item("collageHeader", "collageHeader", span = StaggeredGridItemSpan.FullLine) {
                    header(this)
                }
            }
            for ((clusterIndex, cluster) in state.withIndex()) {
                item("item:$clusterIndex:header", "header", span = StaggeredGridItemSpan.FullLine) {
                    ClusterHeader(
                        modifier = Modifier
                            .animateItemPlacement()
                            .recomposeHighlighter(),
                        state = cluster,
                        title = cluster.displayTitle.ifEmpty { stringResource(string.no_date) },
                        location = cluster.location?.takeIf { cluster.displayTitle.isNotEmpty() },
                        showSelectionHeader = showSelectionHeader,
                        onRefreshClicked = {
                            onClusterRefreshClicked(cluster)
                        }
                    ) {
                        onClusterSelectionClicked(cluster)
                    }
                }

                for (cel in cluster.cels) {
                    item("item:$clusterIndex:" + cel.mediaItem.id.value) {
                        val aspectRatio = when {
                            maintainAspectRatio -> cel.mediaItem.ratio
                            else -> 1f
                        }
                        Cel(
                            modifier = Modifier.animateItemPlacement(),
                            state = cel,
                            onSelected = onCelSelected,
                            onLongClick = onCelLongPressed,
                            aspectRatio = aspectRatio,
                            showSyncState = showSyncState,
                            contentScale = when {
                                maintainAspectRatio -> ContentScale.FillBounds
                                else -> ContentScale.Crop
                            },
                            miniIcons = miniIcons,
                        )
                    }
                }
            }
            item("contentPaddingBottom", "contentPadding", span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
            }
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
        ScrollbarThumb(
            contentPadding,
            gridState,
            showScrollbarHint,
            scrollText
        )
    }
}

@Composable
private fun ScrollbarThumb(
    contentPadding: PaddingValues,
    gridState: LazyStaggeredGridState,
    showScrollbarHint: Boolean,
    scrollText: String
) {
    Box(
        modifier = Modifier
            .recomposeHighlighter()
            .padding(contentPadding)
    ) {
        val primary = MaterialTheme.colors.primary
        val thumbColor = remember {
            primary.copy(alpha = 0.7f)
        }
        InternalLazyStaggeredGridScrollbar(
            gridState = gridState,
            thickness = 8.dp,
            selectionMode = ScrollbarSelectionMode.Thumb,
            thumbColor = thumbColor,
            thumbSelectedColor = primary,
        ) { _, isThumbSelected ->
            val show = showScrollbarHint && isThumbSelected
            AnimatedVisibility(
                modifier = Modifier.padding(end = 8.dp),
                enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(1f, 0.5f)),
                exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(1f, 0.5f)),
                visible = show,
            ) {
                Box(modifier = Modifier.padding(end = 52.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colors.onBackground.copy(alpha = 0.8f))
                            .padding(8.dp)
                            .animateContentSize(),
                    ) {
                        val haptic = LocalHapticFeedback.current
                        Text(
                            text = scrollText,
                            color = MaterialTheme.colors.onPrimary,
                        )
                        LaunchedEffect(scrollText) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.StickyHeader(
    firstOffscreenCluster: Int?,
    topPadding: Dp,
    state: List<Cluster>,
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
        ClusterHeader(
            modifier = Modifier
                .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
                .clickable(interactionSource = interactionSource, indication = null) {},
            state = cluster,
            showSelectionHeader = showSelectionHeader,
            onRefreshClicked = {
                onClusterRefreshClicked(cluster)
            }
        ) {
            onClusterSelectionClicked(cluster)
        }
    }
}