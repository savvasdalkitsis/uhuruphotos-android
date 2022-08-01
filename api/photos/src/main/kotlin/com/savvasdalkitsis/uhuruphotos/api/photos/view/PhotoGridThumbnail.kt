package com.savvasdalkitsis.uhuruphotos.api.photos.view

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
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoGrid
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.CustomColors

@Composable
fun PhotoGridThumbnail(
    modifier: Modifier = Modifier,
    photoGrid: PhotoGrid,
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
            GridItem(photoGrid.photo1)
            GridItem(photoGrid.photo2)
        }
        Row {
            GridItem(photoGrid.photo3)
            GridItem(photoGrid.photo4)
        }
    }
}

@Composable
private fun RowScope.GridItem(photo: Photo?) {
    if (photo != null) {
        PhotoThumbnail(
            modifier = Modifier
                .weight(1f),
            photo = photo,
            onPhotoSelected = { _, _, _ -> },
            aspectRatio = 1f,
            contentScale = ContentScale.Crop,
            photoPadding = 0.dp,
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
