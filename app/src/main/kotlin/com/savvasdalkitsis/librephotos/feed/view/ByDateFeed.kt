package com.savvasdalkitsis.librephotos.feed.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.photos.model.Photo

@Composable
fun ByDateFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    onPhotoSelected: (Photo) -> Unit
) {
    val columns = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 8
        else -> 3
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        albums.forEach { album ->
            item(album.id + "header") {
                Column(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 16.dp,
                        bottom = 16.dp,
                    ),
                ) {
                    Text(
                        text = album.date,
                        style = TextStyle.Default.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    )
                    album.location.takeIf { !it.isNullOrEmpty() }?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = TextStyle.Default.copy(fontWeight = FontWeight.Light)
                        )
                    }
                }
            }
            item(album.id) {
                Column {
                    val evenRows = album.photos.size / columns
                    val rows = evenRows + if (album.photos.size % columns == 0) 0 else 1
                    for (row in 0 until rows) {
                        Row {
                            for (column in 0 until columns) {
                                val photo = album.photos.getOrNull(row * columns + column)
                                if (photo != null) {
                                    PhotoThumbnail(
                                        modifier = Modifier
                                            .weight(photo.ratio),
                                        photo = photo,
                                        onPhotoSelected = onPhotoSelected
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

