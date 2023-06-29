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
package com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.DefaultRequestOptions
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Size
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader

@Composable
fun ThumbnailImage(
    modifier: Modifier = Modifier,
    url: String?,
    contentScale: ContentScale,
    placeholder: Int? = null,
    contentDescription: String?,
    onSuccess: () -> Unit = {},
) {
    var size by remember {
        mutableStateOf<IntSize?>(null)
    }
    val bg = placeholder?.let { ColorPainter(Color(it)) }
    AsyncImage(
        modifier = modifier
            .onGloballyPositioned { size = it.size },
        imageLoader = LocalThumbnailImageLoader.current,
        model = url.toRequest(size?.toSize) {
            onSuccess()
        },
        contentScale = contentScale,
        placeholder = bg,
        error = bg,
        fallback = bg,
        contentDescription = contentDescription,
    )
}

@Composable
fun FullSizeImage(
    modifier: Modifier = Modifier,
    lowResUrl: String?,
    fullResUrl: String?,
    onFullResImageLoaded: () -> Unit = {},
    contentScale: ContentScale,
    placeholder: Painter? = null,
    contentDescription: String?,
) {
    var showLowRes = remember { true }

    Box(modifier = modifier) {
        if (showLowRes) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                imageLoader = LocalFullImageLoader.current,
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(lowResUrl)
                    .build(),
                contentScale = contentScale,
                placeholder = placeholder,
                contentDescription = null,
            )
        }
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            imageLoader = LocalFullImageLoader.current,
            model = fullResUrl.toRequest(size = Size.ORIGINAL, precision = Precision.EXACT) {
                showLowRes = false
                onFullResImageLoaded()
            },
            contentScale = contentScale,
            contentDescription = contentDescription,
        )
    }
}

private val IntSize.toSize get() = Size(width, height)

@Composable
private fun String?.toRequest(
    size: Size?,
    precision: Precision = Precision.INEXACT,
    onSuccess: () -> Unit
) = this?.let { url ->
    if (size != null) {
        ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(size)
            .diskCachePolicy(CachePolicy.ENABLED)
            .allowHardware(true)
            .crossfade(true)
            .defaults(
                DefaultRequestOptions(
                    precision = precision,
                )
            )
            .listener(onSuccess = { _, _ -> onSuccess() })
            .build()
    } else {
        null
    }
}