/*
Copyright 2023 Savvas Dalkitsis

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

package com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.request.SuccessResult
import me.saket.telephoto.zoomable.ZoomableImage
import me.saket.telephoto.zoomable.ZoomableImageState

@Composable
fun FullSizeImage(
    modifier: Modifier = Modifier,
    lowResUrl: String?,
    fullResUrl: String?,
    onFullResImageLoaded: () -> Unit = {},
    contentScale: ContentScale,
    onClick: () -> Unit,
    contentDescription: String?,
    zoomableState: ZoomableImageState,
) {
    val context = LocalContext.current
    ZoomableImage(
        modifier = modifier
            .fillMaxSize(),
        image = zoomableImageSourceWithPlaceholder(lowResUrl, ImageRequest.Builder(context)
            .data(fullResUrl)
            .listener(remember {
                object : ImageRequest.Listener {
                    override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                        onFullResImageLoaded()
                    }
                }
            })
        ),
        state = zoomableState,
        onClick = { onClick() },
        contentScale = contentScale,
        contentDescription = contentDescription,
    )
}