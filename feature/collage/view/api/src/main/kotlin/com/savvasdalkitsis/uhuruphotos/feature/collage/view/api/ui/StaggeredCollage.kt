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

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import my.nanihadesuka.compose.ScrollbarSelectionMode

@Composable
internal fun StaggeredCollage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: List<Cluster>,
    showSelectionHeader: Boolean = false,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    columnCount: Int,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    collageHeader: @Composable (LazyStaggeredGridItemScope.() -> Unit)? = null,
    onCelSelected: CelSelected,
    onCelLongPressed: (CelState) -> Unit,
    onClusterRefreshClicked: (Cluster) -> Unit,
    onClusterSelectionClicked: (Cluster) -> Unit,
) {
    Box {
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
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
            }
            collageHeader?.let { header ->
                item("collageHeader", "collageHeader", span = StaggeredGridItemSpan.FullLine) {
                    header(this)
                }
            }
            for (cluster in state) {
                if ((cluster.displayTitle + cluster.location.orEmpty()).isNotEmpty()) {
                    item(cluster.id, "header", span = StaggeredGridItemSpan.FullLine) {
                        ClusterHeader(
                            modifier = Modifier.animateItemPlacement().recomposeHighlighter(),
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

                for (cel in cluster.cels) {
                    item(cel.mediaItem.id.value) {
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
        Box(modifier = Modifier
            .recomposeHighlighter()
            .padding(contentPadding)
        ) {
            InternalLazyStaggeredGridScrollbar(
                gridState = gridState,
                thickness = 8.dp,
                selectionMode = ScrollbarSelectionMode.Thumb,
                thumbColor = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                thumbSelectedColor = MaterialTheme.colors.primary,
            )
        }

    }
}