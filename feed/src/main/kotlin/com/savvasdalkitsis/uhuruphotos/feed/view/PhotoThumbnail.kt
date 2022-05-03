/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.toColor
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.model.SelectionMode
import com.savvasdalkitsis.uhuruphotos.ui.theme.CustomColors

@Composable
fun PhotoThumbnail(
    modifier: Modifier = Modifier,
    photo: Photo,
    onPhotoSelected: PhotoSelected,
    aspectRatio: Float = photo.ratio,
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
            .aspectRatio(aspectRatio)
            .padding(1.dp)
            .background(
                if (photo.selectionMode == SelectionMode.SELECTED)
                    Color.LightGray
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
                    tint = Color.White,
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
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(visible = photo.selectionMode != SelectionMode.UNDEFINED) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(if (photo.selectionMode == SelectionMode.SELECTED)
                        Color.White
                    else
                        Color.Transparent
                    ),
                painter = painterResource(id = if (photo.selectionMode == SelectionMode.SELECTED)
                    R.drawable.ic_check_circle
                else
                    R.drawable.ic_outline_unselected
                ),
                tint = if (photo.selectionMode == SelectionMode.SELECTED)
                    CustomColors.selected
                else
                    Color.White,
                contentDescription = null
            )
        }
    }

    LaunchedEffect(photo.id, photo.selectionMode) {
        if (photo.selectionMode == SelectionMode.SELECTED) {
            scale.animateTo(0.85f)
        } else {
            scale.animateTo(1f)
        }
    }
}

typealias PhotoSelected = (photo: Photo, center: Offset, scale: Float) -> Unit