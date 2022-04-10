package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.extensions.toColor
import com.savvasdalkitsis.librephotos.photos.model.Photo

@Composable
fun PhotoThumbnail(
    modifier: Modifier = Modifier,
    photo: Photo,
    onPhotoSelected: (Photo) -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(photo.ratio)
            .padding(1.dp)
            .background(photo.fallbackColor.toColor())
            .clickable { onPhotoSelected(photo) }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = photo.url,
            contentScale = ContentScale.FillBounds,
            contentDescription = "photo",
        )
        if (photo.isVideo) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_play_filled),
                contentDescription = null
            )
        }
    }
}