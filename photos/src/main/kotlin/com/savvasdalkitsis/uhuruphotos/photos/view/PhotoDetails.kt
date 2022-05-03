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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.radusalagean.infobarcompose.InfoBar
import com.radusalagean.infobarcompose.InfoBarMessage
import com.savvasdalkitsis.uhuruphotos.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.PhotoSheetStyle.BOTTOM
import com.savvasdalkitsis.uhuruphotos.photos.view.PhotoSheetStyle.SIDE
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.zoom.ZoomableState
import com.savvasdalkitsis.uhuruphotos.ui.view.zoom.zoomable
import com.savvasdalkitsis.uhuruphotos.video.view.Video

@Composable
fun PhotoDetails(
    zoomableState: ZoomableState,
    action: (PhotoAction) -> Unit,
    state: PhotoState,
    contentPadding: PaddingValues
) {
    val photoSheetStyle = LocalPhotoSheetStyle.current

    fun showInfoIfSheetStyle(expected: PhotoSheetStyle): Boolean =
        if (photoSheetStyle == expected) {
            action(PhotoAction.ShowInfo)
            true
        } else {
            false
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .zoomable(
                zoomableState = zoomableState,
                onTap = { action(PhotoAction.ToggleUI) },
                onSwipeAway = { action(PhotoAction.NavigateBack) },
                onSwipeUp = { showInfoIfSheetStyle(BOTTOM) },
                onSwipeToStart = { showInfoIfSheetStyle(SIDE) },
            )
    ) {
        when {
            state.isVideo -> Video(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                videoUrl = state.fullResUrl,
                videoThumbnailUrl = state.lowResUrl,
                play = true,
            )
            else -> Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                lowResUrl = state.lowResUrl,
                fullResUrl = state.fullResUrl,
                onFullResImageLoaded = { action(PhotoAction.FullImageLoaded) },
                contentScale = ContentScale.Fit,
                contentDescription = "photo",
            )
        }
        Column {
            Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
            InfoBar(offeredMessage = state.errorMessage?.let { InfoBarMessage(it) }) {
                action(PhotoAction.DismissErrorMessage)
            }
        }
        if (state.showPhotoDeletionConfirmationDialog) {
            DeletePermissionDialog(
                photoCount = 1,
                onDismiss = { action(PhotoAction.DismissPhotoDeletionDialog) }
            ) { action(PhotoAction.DeletePhoto) }
        }
    }
}