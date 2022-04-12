package com.savvasdalkitsis.librephotos.feed.view

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.photos.model.Photo
import com.savvasdalkitsis.librephotos.ui.view.LazyStaggeredGrid

@Composable
fun FullFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    onPhotoSelected: (Photo, Offset) -> Unit,
) {
    LazyStaggeredGrid(
        modifier = modifier
            .padding(start = 1.dp, end = 1.dp),
        columnCount = when (LocalConfiguration.current.orientation) {
            ORIENTATION_LANDSCAPE -> 5
            else -> 2
        },
        contentPadding = contentPadding,
    ) {
        albums.flatMap { it.photos }.forEach { photo ->
            item(key = photo.id) {
                PhotoThumbnail(
                    modifier = Modifier.fillMaxSize(),
                    photo = photo,
                    onPhotoSelected = onPhotoSelected
                )
            }
        }
    }
}