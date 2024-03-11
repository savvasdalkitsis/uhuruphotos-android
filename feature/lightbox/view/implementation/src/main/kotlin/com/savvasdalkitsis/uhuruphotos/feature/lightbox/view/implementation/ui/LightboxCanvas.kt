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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.radusalagean.infobarcompose.InfoBar
import com.radusalagean.infobarcompose.InfoBarMessage
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DeleteLocalKeepRemoteMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissUpsellDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.FullMediaDataLoaded
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.RestoreMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ToggleUI
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.TrashMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.UpPressed
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeleteFullySyncedPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.UploadErrorDialog
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.state.UploadErrorDialogMode
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.FullSizeImage
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpsellDialog
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui.Video
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import me.saket.telephoto.zoomable.ZoomableState
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun LightboxCanvas(
    action: (LightboxAction) -> Unit,
    state: LightboxState,
    index: Int,
    contentPadding: PaddingValues,
    scrollState: ScrollState,
    lightboxAlpha: MutableFloatState,
    zoomableState: ZoomableState
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        var refreshing by remember {
            mutableStateOf(false)
        }
        val refreshHeight = 48.dp
        val refreshState = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = {
                refreshing = true
                action(UpPressed)
            },
            refreshThreshold = refreshHeight,
        )
        lightboxAlpha.floatValue = if (refreshing) 0f else 1 - refreshState.progress
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .recomposeHighlighter()
                .pullRefresh(refreshState)
                .verticalScroll(scrollState)
        ) {
            Spacer(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxWidth()
                    .height(refreshState.progress * refreshHeight),
            )
            val mediaItem = state.media[index]
            val serverUrl = LocalServerUrl.current
            Box(modifier = Modifier
                .requiredWidth(this@BoxWithConstraints.maxWidth)
                .requiredHeight(this@BoxWithConstraints.maxHeight)
                .fillMaxSize()
            ) {
                when {
                    mediaItem.id.isVideo -> Box(modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomableState,
                            onClick = { action(ToggleUI) }
                        )) {
                        Video(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            videoUrl = remember(serverUrl, mediaItem.id) {
                                mediaItem.id.fullResUri(serverUrl)
                            },
                            videoThumbnailUrl = remember(serverUrl, mediaItem.id) {
                                mediaItem.id.thumbnailUri(serverUrl)
                            },
                            play = true,
                            onFinishedLoading = { action(FullMediaDataLoaded(mediaItem)) },
                        )
                    }
                    else -> FullSizeImage(
                        modifier = Modifier
                            .recomposeHighlighter()
                            .fillMaxSize()
                            .align(Alignment.Center),
                        lowResUrl = remember(serverUrl, mediaItem.id) {
                            mediaItem.id.thumbnailUri(serverUrl)
                        },
                        fullResUrl = remember(serverUrl, mediaItem.id) {
                            mediaItem.id.fullResUri(serverUrl)
                        },
                        onFullResImageLoaded = { action(FullMediaDataLoaded(mediaItem)) },
                        contentScale = ContentScale.Fit,
                        contentDescription = stringResource(string.photo),
                        zoomableState = rememberZoomableImageState(zoomableState),
                        onClick = { action(ToggleUI) },
                    )
                }
            }
            LightboxDetailsSheet(
                state = state,
                index = index,
                action = action
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
        if (state.showCannotUploadDialog) {
            UploadErrorDialog(
                mode = UploadErrorDialogMode.NOT_ALLOWED,
            ) { action(DismissConfirmationDialogs) }
        }
        if (state.showCannotCheckUploadStatusDialog) {
            UploadErrorDialog(
                mode = UploadErrorDialogMode.ERROR_CHECKING,
            ) { action(DismissConfirmationDialogs) }
        }
        if (state.showUpsellDialog) {
            UpsellDialog(
                onDismiss = { action(DismissUpsellDialog) }
            ) { action(Login) }
        }
        val permissionLauncher = rememberPermissionFlowRequestLauncher()
        if (state.missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(state.missingPermissions.toTypedArray())
        }
    }
}