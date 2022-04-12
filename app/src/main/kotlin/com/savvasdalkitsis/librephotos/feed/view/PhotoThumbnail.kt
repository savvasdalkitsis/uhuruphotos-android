package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.extensions.toColor
import com.savvasdalkitsis.librephotos.log.log
import com.savvasdalkitsis.librephotos.photos.model.Photo

@Composable
fun PhotoThumbnail(
    modifier: Modifier = Modifier,
    photo: Photo,
    onPhotoSelected: (Photo, Offset) -> Unit,
    ratio: Float = photo.ratio,
    contentScale: ContentScale = ContentScale.FillBounds,
) {
    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    var relativeOffset by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .aspectRatio(ratio)
            .padding(1.dp)
            .background(photo.fallbackColor.toColor())
            .onGloballyPositioned { coords ->
                val screenWidth = configuration.screenWidthDp.toFloat() * screenDensity
                val screenHeight = configuration.screenHeightDp.toFloat() * screenDensity
                val center = coords.boundsInWindow().center
                relativeOffset = Offset(center.x / screenWidth, center.y / screenHeight)
            }
            .clickable { onPhotoSelected(photo, relativeOffset) }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = photo.url,
            contentScale = contentScale,
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