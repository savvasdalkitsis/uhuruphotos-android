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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

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
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.AllowStorageManagement
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DeleteLocalKeepRemoteMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.FullMediaDataLoaded
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.RestoreMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ShowInfo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ToggleUI
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.TrashMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Image
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui.Video
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
fun LightboxCanvas(
    action: (LightboxAction) -> Unit,
    state: LightboxState,
    index: Int,
    contentPadding: PaddingValues
) {
    val permissionLauncher = rememberPermissionFlowRequestLauncher()
    val zoomState = rememberZoomableState(
        minScale = 0.6f,
        maxScale = 6f,
        overZoomConfig = OverZoomConfig(1f, 4f)
    )
    // reflection call until, hopefully, this is implemented:
    // https://github.com/mxalbert1996/Zoomable/issues/17
    val dismissDragOffsetY by remember {
        derivedStateOf {
            zoomState.javaClass.declaredMethods.find { it.name == "getDismissDragOffsetY\$zoomable_release" }!!
        }
    }

    val offset by remember {
        derivedStateOf {
            dismissDragOffsetY.invoke(zoomState) as Float
        }
    }
    val density = LocalDensity.current
    val showInfo by remember {
        derivedStateOf {
            with(density) { offset.toDp() < (-64).dp }
        }
    }
    val navigateBack by remember {
        derivedStateOf {
            with(density) { offset.toDp() > 64.dp }
        }
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
            if (state.showFullySyncedDeleteConfirmationDialog) {
                DeleteFullySyncedPermissionDialog(
                    onDismiss = { action(DismissConfirmationDialogs) },
                    onDeleteLocalTrashRemote =  { action(TrashMediaItem) },
                    onDeleteLocal =  { action(DeleteLocalKeepRemoteMediaItem) },
                )
            }
            if (state.showRestorationConfirmationDialog) {
                RestorePermissionDialog(
                    mediaItemCount = 1,
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) { action(RestoreMediaItem) }
            }
            state.showStorageManagementConfirmationDialog?.let { request ->
                AllowStorageManagementDialog(
                    onDismiss = { action(DismissConfirmationDialogs) }
                ) {
                    action(DismissConfirmationDialogs)
                    action(AllowStorageManagement(request))
                }
            }
            if (state.missingPermissions.isNotEmpty()) {
                permissionLauncher.launch(state.missingPermissions.toTypedArray())
            }
        }
    }
}