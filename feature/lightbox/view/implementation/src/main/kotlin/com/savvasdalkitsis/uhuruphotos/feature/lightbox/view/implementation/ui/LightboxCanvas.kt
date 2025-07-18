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

package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.UpsellDialog
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import me.saket.telephoto.zoomable.ZoomableState
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharedTransitionScope.LightboxCanvas(
    action: (LightboxAction) -> Unit,
    state: LightboxState,
    index: Int,
    contentPadding: PaddingValues,
    scrollState: ScrollState,
    zoomableState: ZoomableState
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        val mediaItem = state.media[index]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .recomposeHighlighter()
                .verticalScroll(scrollState)
        ) {
            Box(modifier = Modifier
                .requiredWidth(this@BoxWithConstraints.maxWidth)
                .requiredHeight(this@BoxWithConstraints.maxHeight)
                .fillMaxSize()
            ) {
                LightboxCanvasContent(mediaItem, zoomableState, this@LightboxCanvas, action)
            }
            LightboxDetailsSheet(mediaItem, state.showRestoreButton, action)
        }

        Column {
            Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
            InfoBar(offeredMessage = state.errorMessage?.let {
                InfoBarMessage(text = stringResource(it))
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