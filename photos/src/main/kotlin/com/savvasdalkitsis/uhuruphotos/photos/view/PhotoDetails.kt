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
package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
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
import com.savvasdalkitsis.uhuruphotos.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.*
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.video.view.Video

@Composable
fun PhotoDetails(
    action: (PhotoAction) -> Unit,
    state: PhotoState,
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
            val photo = state.photos[index]
            when {
                photo.isVideo -> Video(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    videoUrl = photo.fullResUrl,
                    videoThumbnailUrl = photo.lowResUrl,
                    play = true,
                )
                else -> Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    lowResUrl = photo.lowResUrl,
                    fullResUrl = photo.fullResUrl,
                    onFullResImageLoaded = { action(FullImageLoaded(photo.id)) },
                    contentScale = ContentScale.Fit,
                    contentDescription = stringResource(R.string.photo),
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
            if (state.showPhotoDeletionConfirmationDialog) {
                DeletePermissionDialog(
                    photoCount = 1,
                    onDismiss = { action(DismissPhotoDeletionDialog) }
                ) { action(DeletePhoto) }
            }
        }
    }
}