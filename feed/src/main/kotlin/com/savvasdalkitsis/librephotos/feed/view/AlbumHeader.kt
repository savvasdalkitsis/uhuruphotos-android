package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.librephotos.albums.model.Album

@Composable
fun AlbumHeader(album: Album) {
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
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
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