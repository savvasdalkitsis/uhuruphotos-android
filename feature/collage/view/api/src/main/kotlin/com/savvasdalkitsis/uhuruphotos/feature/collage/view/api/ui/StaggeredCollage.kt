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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CelRowSlot.CelSlot
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CelRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode

@Composable
internal fun StaggeredCollage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: List<Cluster>,
    showSelectionHeader: Boolean = false,
    showAlbumRefreshButton: Boolean = false,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    columnCount: Int,
    shouldAddEmptyPhotosInRows: Boolean,
    listState: LazyListState = rememberLazyListState(),
    collageHeader: @Composable (LazyItemScope.() -> Unit)? = null,
    onCelSelected: CelSelected,
    onCelLongPressed: (CelState) -> Unit,
    onClusterRefreshClicked: (Cluster) -> Unit,
    onClusterSelectionClicked: (Cluster) -> Unit,
) {
    Box {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
        ) {
            collageHeader?.let { header ->
                item("collageHeader", "collageHeader") {
                    header(this)
                }
            }
            state.forEach { cluster ->
                if ((cluster.displayTitle + cluster.location.orEmpty()).isNotEmpty()) {
                    item(cluster.id, "header") {
                        ClusterHeader(
                            modifier = Modifier.animateItemPlacement(),
                            state = cluster,
                            showSelectionHeader = showSelectionHeader,
                            showRefreshButton = showAlbumRefreshButton,
                            onSelectionHeaderClicked = {
                                onClusterSelectionClicked(cluster)
                            },
                            onRefreshClicked = {
                                onClusterRefreshClicked(cluster)
                            }
                        )
                    }
                }
                val (slots, rows) = if (shouldAddEmptyPhotosInRows) {
                    val emptyPhotos = (columnCount - cluster.cels.size % columnCount) % columnCount
                    val paddedSlots = cluster.cels.map(::CelSlot) + List(emptyPhotos) { EmptySlot }
                    paddedSlots to paddedSlots.size / columnCount
                } else {
                    val evenRows = cluster.cels.size / columnCount
                    cluster.cels.map(::CelSlot) to evenRows + if (cluster.cels.size % columnCount == 0) 0 else 1
                }
                for (row in 0 until rows) {
                    val slotsInRow = (0 until columnCount).mapNotNull { column ->
                        slots.getOrNull(row * columnCount + column)
                    }.toTypedArray()
                    item(
                        slotsInRow.firstNotNullOf { it as? CelSlot }.cel.mediaItem.id.value,
                        "photoRow"
                    ) {
                        CelRow(
                            modifier = Modifier
                                .animateContentSize()
                                .animateItemPlacement(),
                            showSyncState = showSyncState,
                            miniIcons = miniIcons,
                            maintainAspectRatio = maintainAspectRatio,
                            onCelSelected = onCelSelected,
                            onCelLongPressed = onCelLongPressed,
                            slots = slotsInRow
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier
            .padding(contentPadding)
        ) {
            LazyColumnScrollbar(
                listState = listState,
                thickness = 8.dp,
                selectionMode = ScrollbarSelectionMode.Thumb,
                thumbColor = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                thumbSelectedColor = MaterialTheme.colors.primary,
            )
        }
    }
}