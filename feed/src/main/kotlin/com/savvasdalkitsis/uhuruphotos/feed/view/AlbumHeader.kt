package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon

@Composable
fun AlbumHeader(
    modifier: Modifier,
    album: Album,
    showSelectionHeader: Boolean,
    onSelectionHeaderClicked: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(visible = showSelectionHeader) {
            ActionIcon(
                onClick = onSelectionHeaderClicked,
                icon = when {
                    album.photos.any { !it.isSelected } -> R.drawable.ic_check_circle
                    else -> R.drawable.ic_clear
                }
            )
        }
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
}