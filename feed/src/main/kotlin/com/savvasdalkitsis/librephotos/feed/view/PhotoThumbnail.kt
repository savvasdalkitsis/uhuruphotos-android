package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
    onLongClick: (Photo) -> Unit = {},
) {
    val scale = remember(photo.id) { Animatable(1f) }
    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    var relativeCenter by remember(photo.id) { mutableStateOf(Offset.Zero) }
    var relativeScale by remember(photo.id) { mutableStateOf(0f) }
    Box(
        modifier = modifier
            .aspectRatio(ratio)
            .padding(1.dp)
            .background(if (photo.isSelected)
                MaterialTheme.colors.primary
            else
                photo.fallbackColor.toColor()
            )
            .combinedClickable(
                onClick = { onPhotoSelected(photo, relativeCenter, relativeScale) },
                onLongClick = { onLongClick(photo) }
            )
    ) {
        Box(
            modifier = Modifier
                .scale(scale.value)
                .onGloballyPositioned { coordinates ->
                    val screenWidth = configuration.screenWidthDp.toFloat() * screenDensity
                    val boundsInWindow = coordinates.boundsInWindow()
                    relativeCenter = boundsInWindow.center
                    relativeScale = boundsInWindow.width / screenWidth
                }
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                url = photo.thumbnailUrl,
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

    LaunchedEffect(photo.id, photo.isSelected) {
        if (photo.isSelected) {
            scale.animateTo(0.8f)
        } else {
            scale.animateTo(1f)
        }
    }
}

typealias PhotoSelected = (photo: Photo, center: Offset, scale: Float) -> Unit