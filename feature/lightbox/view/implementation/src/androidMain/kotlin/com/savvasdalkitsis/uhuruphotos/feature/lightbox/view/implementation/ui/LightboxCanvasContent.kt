/*
Copyright 2024 Savvas Dalkitsis

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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.FullMediaDataLoaded
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ToggleUI
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.FullSizeImage
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui.Video
import me.saket.telephoto.zoomable.ZoomableState
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.zoomable

@Composable
internal fun BoxScope.LightboxCanvasContent(
    mediaItem: SingleMediaItemState,
    zoomableState: ZoomableState,
    action: (LightboxAction) -> Unit
) {
    val serverUrl = LocalServerUrl.current
    val lowResUrl = remember(serverUrl, mediaItem.id) {
        mediaItem.id.thumbnailUri(serverUrl)
    }
    val fullResUrl = remember(serverUrl, mediaItem.id) {
        mediaItem.id.fullResUri(serverUrl)
    }
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
                videoThumbnailUrl = lowResUrl,
                videoUrl = fullResUrl,
                play = true,
                onFinishedLoading = { action(FullMediaDataLoaded(mediaItem)) },
            )
        }

        else -> FullSizeImage(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxSize()
                .align(Alignment.Center),
            lowResUrl = lowResUrl,
            fullResUrl = fullResUrl,
            onFullResImageLoaded = { action(FullMediaDataLoaded(mediaItem)) },
            contentScale = ContentScale.Fit,
            contentDescription = stringResource(R.string.photo),
            zoomableState = rememberZoomableImageState(zoomableState),
            onClick = { action(ToggleUI) },
        )
    }
}