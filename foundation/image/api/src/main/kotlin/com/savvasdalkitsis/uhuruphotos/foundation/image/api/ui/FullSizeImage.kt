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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import me.saket.telephoto.zoomable.ZoomableImageState
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage

@Composable
fun FullSizeImage(
    modifier: Modifier = Modifier,
    lowResUrl: String?,
    fullResUrl: String?,
    onFullResImageLoaded: () -> Unit = {},
    contentScale: ContentScale,
    onClick: () -> Unit,
    placeholder: Painter? = null,
    contentDescription: String?,
    zoomableState: ZoomableImageState,
) {
    var showLowRes = remember { true }

    val context = LocalContext.current
    Box(modifier = modifier) {
        if (showLowRes) {
            ZoomableAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                imageLoader = LocalThumbnailImageLoader.current,
                state = zoomableState,
                onClick = { onClick() },
                model = ImageRequest.Builder(context)
                    .data(lowResUrl)
                    .build(),
                contentScale = contentScale,
                contentDescription = contentDescription,
            )
        }
        ZoomableAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            imageLoader = LocalFullImageLoader.current,
            state = zoomableState,
            onClick = { onClick() },
            model = ImageRequest.Builder(context)
                .data(fullResUrl)
                .listener(remember {
                    object : ImageRequest.Listener {
                        override fun onSuccess(request: ImageRequest, result: SuccessResult)  {
                            showLowRes = false
                            onFullResImageLoaded()
                        }
                    }
                })
                .build(),
            contentScale = contentScale,
            contentDescription = contentDescription,
        )
    }
}