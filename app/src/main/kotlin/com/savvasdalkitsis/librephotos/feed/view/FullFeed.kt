package com.savvasdalkitsis.librephotos.feed.view

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.ui.view.LazyStaggeredGrid

@Composable
fun FullFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    onPhotoSelected: PhotoSelected,
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