package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.photos.model.Photo

@Composable
fun GridDateFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    columnCount: Int,
    onPhotoSelected: (Photo) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columnCount),
        contentPadding = contentPadding,
    ) {
        albums.forEach { album ->
            item(album.id, span = { GridItemSpan(columnCount) }) {
                AlbumHeader(album)
            }
            for (photo in album.photos) {
                item(photo.url.orEmpty()) {
                    PhotoThumbnail(
                        onPhotoSelected = onPhotoSelected,
                        photo = photo,
                        ratio = 1f,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}