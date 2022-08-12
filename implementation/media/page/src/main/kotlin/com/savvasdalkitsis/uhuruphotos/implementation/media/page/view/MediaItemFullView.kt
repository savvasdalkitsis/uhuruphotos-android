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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.radusalagean.infobarcompose.InfoBar
import com.radusalagean.infobarcompose.InfoBarMessage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.api.media.page.view.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.view.Video
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.FullMediaDataLoaded
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.RestoreMediaItem
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.ShowInfo
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.ToggleUI
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.TrashMediaItem
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.MediaItemPageState

@Composable
fun MediaItemFullView(
    action: (com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction) -> Unit,
    state: MediaItemPageState,
    index: Int,
    contentPadding: PaddingValues
) {
    val zoomState = rememberZoomableState(
        minScale = 0.6f,
        maxScale = 6f,
        overZoomConfig = OverZoomConfig(1f, 4f)
    )
    // reflection call until, hopefully, this is implemented:
    // https://github.com/mxalbert1996/Zoomable/issues/17
    val dismissDragOffsetY = remember {
        zoomState.javaClass.declaredMethods.find { it.name == "getDismissDragOffsetY\$zoomable_release" }!!
    }

    val offset by derivedStateOf {
        dismissDragOffsetY.invoke(zoomState) as Float
    }
    val density = LocalDensity.current
    val showInfo by derivedStateOf {
        with(density) { offset.toDp() < (-64).dp }
    }
    val navigateBack by derivedStateOf {
        with(density) { offset.toDp() > 64.dp }
    }

    LaunchedEffect(showInfo) {
        if (showInfo) {
            action(ShowInfo)
        }
    }
    LaunchedEffect(navigateBack) {
        if (navigateBack) {
            action(NavigateBack)
        }
    }
    Zoomable(
        state = zoomState,
        onTap = { action(ToggleUI) },
        dismissGestureEnabled = true,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val mediaItem = state.media[index]
            when {
                mediaItem.isVideo -> Video(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    videoUrl = mediaItem.fullResUrl,
                    videoThumbnailUrl = mediaItem.lowResUrl,
                    play = true,
                    onFinishedLoading = { action(FullMediaDataLoaded(mediaItem)) },
                )
                else -> Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    lowResUrl = mediaItem.lowResUrl,
                    fullResUrl = mediaItem.fullResUrl,
                    onFullResImageLoaded = { action(FullMediaDataLoaded(mediaItem)) },
                    contentScale = ContentScale.Fit,
                    contentDescription = stringResource(string.photo),
                )
            }
            Column {
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                InfoBar(offeredMessage = state.errorMessage?.let {
                    InfoBarMessage(textStringResId = it)
                }) {
                    action(DismissErrorMessage)
                }
            }
            if (state.showTrashingConfirmationDialog) {
                TrashPermissionDialog(
                    mediaItemCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(TrashMediaItem) }
            }
            if (state.showDeleteConfirmationDialog) {
                DeletePermissionDialog(
                    mediaItemCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(TrashMediaItem) }
            }
            if (state.showRestorationConfirmationDialog) {
                RestorePermissionDialog(
                    mediaItemCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(RestoreMediaItem) }
            }
        }
    }
}