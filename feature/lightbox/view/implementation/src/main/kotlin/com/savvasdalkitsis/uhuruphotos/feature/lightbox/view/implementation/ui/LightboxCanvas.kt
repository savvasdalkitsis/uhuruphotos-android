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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.radusalagean.infobarcompose.InfoBar
import com.radusalagean.infobarcompose.InfoBarMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DeleteLocalKeepRemoteMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DismissUpsellDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.RestoreMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.TrashMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeleteFullySyncedPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.UploadErrorDialog
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.state.UploadErrorDialogModeState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.PullToDismissSpacer
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.PullToDismissState
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.pullToDismiss
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.UpsellDialog
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import me.saket.telephoto.zoomable.ZoomableState

@Composable
fun LightboxCanvas(
    action: (LightboxAction) -> Unit,
    state: LightboxState,
    index: Int,
    contentPadding: PaddingValues,
    scrollState: ScrollState,
    zoomableState: ZoomableState,
    dismissState: PullToDismissState
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val originalBackground = MaterialTheme.colors.background
        val background by remember {
            derivedStateOf {
                originalBackground.copy(alpha = dismissState.dismissAlpha)
            }
        }

        val mediaItem = state.media[index]
        LightboxPreviousScreenBackground(dismissState) {
            Column(
                modifier = Modifier
                    .alpha(1 - dismissState.postDismissProgress)
                    .fillMaxSize()
                    .background(background)
                    .recomposeHighlighter()
                    .pullToDismiss(dismissState)
                    .verticalScroll(scrollState)
            ) {
                PullToDismissSpacer(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .fillMaxWidth(),
                    dismissState = dismissState,
                )
                Box(modifier = Modifier
                    .scale(0.3f + 0.7f * (1 - dismissState.progress / 2))
                    .offset(y = dismissState.progress * 100.dp)
                    .requiredWidth(this@BoxWithConstraints.maxWidth)
                    .requiredHeight(this@BoxWithConstraints.maxHeight)
                    .fillMaxSize()
                ) {
                    LightboxCanvasContent(mediaItem, zoomableState, action)
                }
                LightboxDetailsSheet(mediaItem, state.showRestoreButton, action)
            }
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
                mode = UploadErrorDialogModeState.NOT_ALLOWED,
            ) { action(DismissConfirmationDialogs) }
        }
        if (state.showCannotCheckUploadStatusDialog) {
            UploadErrorDialog(
                mode = UploadErrorDialogModeState.ERROR_CHECKING,
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