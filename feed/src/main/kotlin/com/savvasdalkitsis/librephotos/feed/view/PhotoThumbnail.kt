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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.image.view.Image
import com.savvasdalkitsis.librephotos.infrastructure.extensions.toColor
import com.savvasdalkitsis.librephotos.photos.model.Photo

@Composable
fun PhotoThumbnail(
    modifier: Modifier = Modifier,
    photo: Photo,
    onPhotoSelected: PhotoSelected,
    ratio: Float = photo.ratio,
    contentScale: ContentScale = ContentScale.FillBounds,
) {
    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    var relativeCenter by remember { mutableStateOf(Offset.Zero) }
    var relativeScale by remember { mutableStateOf(0f) }
    Box(
        modifier = modifier
            .aspectRatio(ratio)
            .padding(1.dp)
            .background(photo.fallbackColor.toColor())
            .onGloballyPositioned { coordinates ->
                val screenWidth = configuration.screenWidthDp.toFloat() * screenDensity
                val boundsInWindow = coordinates.boundsInWindow()
                relativeCenter = boundsInWindow.center
                relativeScale = boundsInWindow.width / screenWidth
            }
            .clickable { onPhotoSelected(photo, relativeCenter, relativeScale) }
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            url = photo.url,
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
        if (photo.isFavourite) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .padding(2.dp),
                painter = painterResource(id = R.drawable.ic_favourite),
                contentDescription = null
            )
        }
    }
}

typealias PhotoSelected = (photo: Photo, center: Offset, scale: Float) -> Unit