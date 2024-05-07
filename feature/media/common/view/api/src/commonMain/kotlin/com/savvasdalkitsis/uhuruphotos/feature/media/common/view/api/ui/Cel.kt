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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionMode.CHECKABLE
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionMode.NONE
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionMode.SELECTABLE
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.toColor
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.IconResource.Image
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.Checkable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.DynamicIcon
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun Cel(
    modifier: Modifier = Modifier,
    state: CelState,
    onSelected: CelSelected,
    aspectRatio: Float = state.mediaItem.ratio,
    contentScale: ContentScale = ContentScale.FillBounds,
    shape: Shape = RectangleShape,
    miniIcons: Boolean = false,
    selectionMode: CelSelectionMode = SELECTABLE,
    showSyncState: Boolean = false,
    onLongClick: (CelState) -> Unit = {},
) {
    @Composable
    fun cel(
        celModifier: Modifier = Modifier,
    ) {
        Cel(
            celModifier,
            state,
            aspectRatio,
            contentScale,
            shape,
            miniIcons,
            showSyncState
        )
    }
    when (selectionMode) {
        CHECKABLE -> Checkable(
            modifier = modifier,
            id = state.mediaItem.id,
            selectionMode = state.selectionMode,
            onClick = { onSelected(state) },
            onLongClick = { onLongClick(state) },
        ) {
            cel()
        }
        SELECTABLE -> cel(celModifier = modifier.clickable { onSelected(state) })
        NONE -> cel(celModifier = modifier)
    }
}

@Composable
private fun Cel(
    modifier: Modifier = Modifier,
    state: CelState,
    aspectRatio: Float = state.mediaItem.ratio,
    contentScale: ContentScale = ContentScale.FillBounds,
    shape: Shape = RectangleShape,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
) {
    Cel(
        modifier,
        aspectRatio,
        contentScale,
        shape,
        miniIcons,
        showSyncState,
        state.mediaItem.fallbackColor,
        state.selectionMode,
        state.mediaItem.id,
        state.mediaItem.isFavourite,
    )
}

@Composable
private fun Cel(
    modifier: Modifier = Modifier,
    aspectRatio: Float,
    contentScale: ContentScale = ContentScale.FillBounds,
    shape: Shape = RectangleShape,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    fallbackColor: String?,
    selectionMode: SelectionMode,
    id: MediaId<*>,
    isFavourite: Boolean,
) {
    val iconSize = remember(miniIcons) {
        if (miniIcons) 16.dp else 24.dp
    }
    val fallback = fallbackColor.toColor()

    val backgroundColor = remember(selectionMode) {
        when (selectionMode) {
            SelectionMode.SELECTED -> Color.LightGray
            else -> fallback
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(shape)
            .recomposeHighlighter()
    ) {
        val serverUrl = LocalServerUrl.current
        Thumbnail(
            modifier = Modifier.fillMaxWidth(),
            url = remember(serverUrl, id) {
                id.thumbnailUri(serverUrl)
            },
            isVideo = false,//mediaItem.id.isVideo,
            contentScale = contentScale,
            placeholder = backgroundColor.toArgb(),
            contentDescription = null
        )
        if (id.isVideo) {
            val size = remember(miniIcons) {
                if (miniIcons) 16.dp else 48.dp
            }
            val alignment = remember(miniIcons) {
                if (miniIcons) BottomStart else Center
            }
            Icon(
                modifier = Modifier
                    .size(size)
                    .align(alignment),
                painter = painterResource(images.ic_play_filled),
                tint = Color.White,
                contentDescription = null
            )
        }
        if (isFavourite) {
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .align(TopEnd)
                    .padding(2.dp),
                painter = painterResource(images.ic_favourite),
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
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .padding(2.dp)
                    .alpha(0.7f),
            ) {
                DynamicIcon(icon = Image(id.syncState.icon), tint = Color.White)
            }
        }
    }
}

typealias CelSelected = (cel: CelState) -> Unit