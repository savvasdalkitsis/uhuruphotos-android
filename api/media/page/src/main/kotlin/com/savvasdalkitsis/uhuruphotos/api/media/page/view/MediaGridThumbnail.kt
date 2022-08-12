package com.savvasdalkitsis.uhuruphotos.api.media.page.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaGrid
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors

@Composable
fun MediaGridThumbnail(
    modifier: Modifier = Modifier,
    mediaGrid: MediaGrid,
    onSelected: () -> Unit,
    shape: Shape = RectangleShape,
) {
    Column(
        modifier = modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(shape)
            .clickable(
                onClick = onSelected,
            ),
    ) {
        Row {
            GridItem(mediaGrid.mediaItem1)
            GridItem(mediaGrid.mediaItem2)
        }
        Row {
            GridItem(mediaGrid.mediaItem3)
            GridItem(mediaGrid.mediaItem4)
        }
    }
}

@Composable
private fun RowScope.GridItem(mediaItem: MediaItem?) {
    if (mediaItem != null) {
        MediaItemThumbnail(
            modifier = Modifier
                .weight(1f),
            mediaItem = mediaItem,
            onItemSelected = { _, _, _ -> },
            aspectRatio = 1f,
            contentScale = ContentScale.Crop,
            itemPadding = 0.dp,
            miniIcons = true,
            selectable = false
        )
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(CustomColors.emptyItem)
        )
    }
}
