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
package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.PhotoRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feed.view.PhotoRowSlot.PhotoSlot
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

@Composable
fun StaggeredDateFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    showSelectionHeader: Boolean = false,
    maintainAspectRatio: Boolean = true,
    columnCount: Int,
    shouldAddEmptyPhotosInRows: Boolean,
    listState: LazyListState = rememberLazyListState(),
    onPhotoSelected: PhotoSelected,
    onPhotoLongPressed: (Photo) -> Unit,
    onAlbumSelectionClicked: (Album) -> Unit,
) {
    Box {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
        ) {
            albums.forEach { album ->
                item(album.id, "header") {
                    AlbumHeader(
                        modifier = Modifier.animateItemPlacement(),
                        album,
                        showSelectionHeader
                    ) {
                        onAlbumSelectionClicked(album)
                    }
                }
                val (slots, rows) = if (shouldAddEmptyPhotosInRows) {
                    val emptyPhotos = (columnCount - album.photos.size % columnCount) % columnCount
                    val paddedSlots = album.photos.map(::PhotoSlot) + List(emptyPhotos) { EmptySlot }
                    paddedSlots to paddedSlots.size / columnCount
                } else {
                    val evenRows = album.photos.size / columnCount
                    album.photos.map(::PhotoSlot) to evenRows + if (album.photos.size % columnCount == 0) 0 else 1
                }
                for (row in 0 until rows) {
                    val slotsInRow = (0 until columnCount).mapNotNull { column ->
                        slots.getOrNull(row * columnCount + column)
                    }.toTypedArray()
                    item(
                        slotsInRow.mapNotNull { it as? PhotoSlot }.first().photo.id,
                        "photoRow"
                    ) {
                        PhotoRow(
                            modifier = Modifier
                                .animateContentSize()
                                .animateItemPlacement(),
                            maintainAspectRatio = maintainAspectRatio,
                            onPhotoSelected = onPhotoSelected,
                            onPhotoLongPressed = onPhotoLongPressed,
                            slots = slotsInRow
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier
            .padding(contentPadding)
        ) {
            /*
            Use our own copy of the scrollbar until
            https://github.com/nanihadesuka/LazyColumnScrollbar/pull/1 is fixed
             */
            com.savvasdalkitsis.uhuruphotos.ui.view.LazyColumnScrollbar(
                listState = listState,
                thickness = 8.dp,
                thumbColor = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                thumbSelectedColor = MaterialTheme.colors.primary,
            )
        }
    }
}