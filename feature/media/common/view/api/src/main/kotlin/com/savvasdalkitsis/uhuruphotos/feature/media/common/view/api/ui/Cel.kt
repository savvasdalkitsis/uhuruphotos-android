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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.toColor
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Image
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui.Video
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Cel(
    modifier: Modifier = Modifier,
    state: CelState,
    onSelected: CelSelected,
    aspectRatio: Float = state.mediaItem.ratio,
    contentScale: ContentScale = ContentScale.FillBounds,
    shape: Shape = RectangleShape,
    itemPadding: Dp = 1.dp,
    miniIcons: Boolean = false,
    selectable: Boolean = true,
    showSyncState: Boolean = false,
    onLongClick: (CelState) -> Unit = {},
) {
    val mediaItem = state.mediaItem
    val scale = remember(mediaItem.id) { Animatable(1f) }
    val iconSize = if (miniIcons) 16.dp else 24.dp
    val fallbackColor = mediaItem.fallbackColor.toColor()
    var loaded by remember {
        mutableStateOf(false)
    }

    val backgroundColor = when {
        state.selectionMode == MediaItemSelectionMode.SELECTED -> Color.LightGray
        loaded -> Color.Transparent
        else -> fallbackColor
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .padding(itemPadding)
            .background(backgroundColor)
            .clip(shape)
            .let {
                if (selectable) {
                    it.combinedClickable(
                        onClick = { onSelected(state) },
                        onLongClick = { onLongClick(state) }
                    )
                } else {
                    it
                }
            }
    ) {
        Box(
            modifier = Modifier
                .scale(scale.value)
        ) {
            val thumbnailUri = mediaItem.thumbnailUri
            val scope = rememberCoroutineScope()
            val exoPlayer = if (thumbnailUri == null)
                null
            else
                LocalExoPlayerProvider.current.maybeCreateExoplayer(thumbnailUri)
            if (!LocalAnimatedVideoThumbnails.current || !mediaItem.isVideo || thumbnailUri == null || exoPlayer == null) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    url = thumbnailUri,
                    contentScale = contentScale,
                    contentDescription = null,
                    onSuccess = {
                        scope.launch {
                            delay(100)
                            loaded = true
                        }
                    }
                )
            } else {
                Video(
                    modifier = Modifier.fillMaxSize(),
                    exoPlayer = exoPlayer,
                    videoUrl = thumbnailUri,
                    videoThumbnailUrl = thumbnailUri,
                    play = true,
                    repeatMode = PlaybackStateCompat.REPEAT_MODE_ONE,
                    showControls = false,
                    showProgress = false,
                    mute = true,
                    crop = true,
                ) {

                }
            }
            if (mediaItem.isVideo) {
                Icon(
                    modifier = Modifier
                        .size(if (miniIcons) 16.dp else 48.dp)
                        .align(if (miniIcons) BottomStart else Center),
                    painter = painterResource(id = drawable.ic_play_filled),
                    tint = Color.White,
                    contentDescription = null
                )
            }
            if (mediaItem.isFavourite) {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .align(TopEnd)
                        .padding(2.dp),
                    painter = painterResource(id = drawable.ic_favourite),
                    tint = Color.White,
                    contentDescription = null
                )
            }
            AnimatedVisibility(
                modifier = Modifier
                    .align(BottomEnd),
                visible = showSyncState,
                enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                exit = fadeOut(animationSpec = tween(delayMillis = 1200, durationMillis = 400))
            ) {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .padding(2.dp)
                        .alpha(0.7f),
                    painter = painterResource(mediaItem.syncState.icon),
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(visible = state.selectionMode != MediaItemSelectionMode.UNDEFINED) {
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(
                        if (state.selectionMode == MediaItemSelectionMode.SELECTED)
                            Color.White
                        else
                            Color.Transparent
                    ),
                painter = painterResource(
                    id = if (state.selectionMode == MediaItemSelectionMode.SELECTED)
                        drawable.ic_check_circle
                    else
                        drawable.ic_outline_unselected
                ),
                tint = if (state.selectionMode == MediaItemSelectionMode.SELECTED)
                    CustomColors.selected
                else
                    Color.White,
                contentDescription = null
            )
        }
    }

    LaunchedEffect(mediaItem.id, state.selectionMode) {
        if (state.selectionMode == MediaItemSelectionMode.SELECTED) {
            scale.animateTo(0.85f)
        } else {
            scale.animateTo(1f)
        }
    }
}

typealias CelSelected = (cel: CelState) -> Unit