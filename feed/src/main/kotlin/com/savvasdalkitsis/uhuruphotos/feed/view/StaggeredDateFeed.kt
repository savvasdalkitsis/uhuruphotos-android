package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        albums.forEach { album ->
            item(album.id) {
                AlbumHeader(
                    modifier = Modifier.animateItemPlacement(),
                    album, showSelectionHeader) {
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
                item(slotsInRow.mapNotNull { it as? PhotoSlot }.first().photo.id) {
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
}