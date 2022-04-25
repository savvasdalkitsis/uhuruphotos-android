package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

@Composable
fun StaggeredDateFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    showSelectionHeader: Boolean = false,
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
            val (photos, rows) = if (shouldAddEmptyPhotosInRows) {
                val emptyPhotos = (columnCount - album.photos.size % columnCount) % columnCount
                val paddedPhotos = album.photos + List(emptyPhotos) { Photo(id = "empty",) }
                paddedPhotos to paddedPhotos.size / columnCount
            } else {
                val evenRows = album.photos.size / columnCount
                album.photos to evenRows + if (album.photos.size % columnCount == 0) 0 else 1
            }
            for (row in 0 until rows) {
                val photosInRow = (0 until columnCount).mapNotNull { column ->
                    photos.getOrNull(row * columnCount + column)
                }.toTypedArray()
                item(photosInRow.first().id) {
                    PhotoRow(
                        modifier = Modifier.animateItemPlacement(),
                        onPhotoSelected = onPhotoSelected,
                        onPhotoLongPressed = onPhotoLongPressed,
                        photos = photosInRow
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoRow(
    modifier: Modifier,
    onPhotoSelected: PhotoSelected,
    onPhotoLongPressed: (Photo) -> Unit,
    vararg photos: Photo
) {
    Row(modifier = modifier) {
        for (photo in photos) {
            PhotoThumbnail(
                modifier = Modifier
                    .weight(photo.ratio),
                photo = photo,
                onPhotoSelected = onPhotoSelected,
                onLongClick = onPhotoLongPressed,
            )
        }
    }
}

