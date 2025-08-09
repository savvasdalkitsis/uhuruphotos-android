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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState.CHECKABLE
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState.NONE
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState.SELECTABLE
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.NewCelState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.SharedElementId
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.sharedElement
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.toColor
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.Checkable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import me.saket.telephoto.zoomable.ZoomablePeekOverlayBackdrop
import me.saket.telephoto.zoomable.rememberZoomablePeekOverlayState
import me.saket.telephoto.zoomable.zoomablePeekOverlay
import org.jetbrains.compose.resources.painterResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_favourite
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_play_filled

@Composable
fun SharedTransitionScope.Cel(
    modifier: Modifier = Modifier,
    state: CelState? = null,
    newState: NewCelState? = null,
    onSelected: CelSelected,
    aspectRatio: Float = newState?.mediaItem?.ratio ?: 1f,
    contentScale: ContentScale = ContentScale.FillBounds,
    contentOffset: Float = 0f,
    shape: Shape = RectangleShape,
    miniIcons: Boolean = false,
    selectionMode: CelSelectionModeState = SELECTABLE,
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
            newState,
            aspectRatio,
            contentScale,
            contentOffset,
            shape,
            miniIcons,
            showSyncState,
        )
    }
    when (selectionMode) {
        CHECKABLE -> Checkable(
            modifier = modifier,
            id = state?.mediaItem?.id ?: newState!!.mediaItem.id,
            selectionMode = state?.selectionMode ?: newState!!.selectionMode,
            onClick = { onSelected(state!!) },
            onLongClick = { onLongClick(state!!) },
        ) {
            cel()
        }
        SELECTABLE -> cel(celModifier = modifier.clickable { onSelected(state) })
        NONE -> cel(celModifier = modifier)
    }
}

@Composable
private fun SharedTransitionScope.Cel(
    modifier: Modifier = Modifier,
    state: CelState?,
    newState: NewCelState?,
    aspectRatio: Float = newState?.mediaItem?.ratio ?: 1f,
    contentScale: ContentScale = ContentScale.FillBounds,
    contentOffset: Float = 0f,
    shape: Shape = RectangleShape,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
) {
    Cel(
        modifier,
        aspectRatio,
        contentScale,
        contentOffset,
        shape,
        miniIcons,
        showSyncState,
        state?.mediaItem?.fallbackColor ?: newState!!.mediaItem.fallbackColor,
        state?.selectionMode ?: newState!!.selectionMode,
        state?.mediaItem?.id ?: newState!!.mediaItem.id,
        state?.mediaItem?.isFavourite ?: newState!!.mediaItem.isFavourite,
    )
}

@Composable
private fun SharedTransitionScope.Cel(
    modifier: Modifier = Modifier,
    aspectRatio: Float,
    contentScale: ContentScale = ContentScale.FillBounds,
    contentOffset: Float = 0f,
    shape: Shape = RectangleShape,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    fallbackColor: String?,
    selectionMode: SelectionMode,
    id: MediaIdModel<*>,
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
            .sharedElement(SharedElementId.imageCanvas(id.mediaHash.hash))
            .recomposeHighlighter()
    ) {
        val serverUrl = LocalServerUrl.current
        val peekState = rememberZoomablePeekOverlayState()

        Thumbnail(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(contentOffset.toInt(), 0) }
                .sharedElement(SharedElementId.image(id.mediaHash.hash))
                .zoomablePeekOverlay(
                    peekState,
                    ZoomablePeekOverlayBackdrop.scrim(backgroundColor.copy(alpha = 0.4f))
                ),
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
                painter = painterResource(drawable.ic_play_filled),
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
                painter = painterResource(drawable.ic_favourite),
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
                UhuruIcon(icon = id.syncState.icon, tint = Color.White)
            }
        }
    }
}

typealias CelSelected = (cel: CelState) -> Unit