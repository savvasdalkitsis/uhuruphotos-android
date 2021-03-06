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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.view

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
import com.savvasdalkitsis.uhuruphotos.api.image.view.Image
import com.savvasdalkitsis.uhuruphotos.api.photos.view.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.api.photos.view.RestorePermissionDialog
import com.savvasdalkitsis.uhuruphotos.api.photos.view.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.video.view.Video
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.TrashPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.FullImageLoaded
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.RestorePhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ShowInfo
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ToggleUI
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState

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
                    onFinishedLoading = { action(FullImageLoaded(photo)) },
                )
                else -> Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    lowResUrl = photo.lowResUrl,
                    fullResUrl = photo.fullResUrl,
                    onFullResImageLoaded = { action(FullImageLoaded(photo)) },
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
            if (state.showPhotoTrashingConfirmationDialog) {
                TrashPermissionDialog(
                    photoCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(TrashPhoto) }
            }
            if (state.showPhotoDeleteConfirmationDialog) {
                DeletePermissionDialog(
                    photoCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(TrashPhoto) }
            }
            if (state.showPhotoRestorationConfirmationDialog) {
                RestorePermissionDialog(
                    photoCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(RestorePhoto) }
            }
        }
    }
}