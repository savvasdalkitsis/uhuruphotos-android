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

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.size.Precision
import coil.size.Size
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.LocalAuthenticationHeaders
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.ScaleType
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.video.playUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Thumbnail(
    modifier: Modifier = Modifier,
    url: String?,
    contentScale: ContentScale,
    placeholder: Int? = null,
    aspectRatio: Float? = null,
    contentDescription: String?,
    respectNetworkCacheHeaders: Boolean = false,
    isVideo: Boolean = false,
    onSuccess: () -> Unit = {},
) {
    val bg = remember(placeholder) {
        placeholder?.let { ColorPainter(Color(it)) }
    }
    val static = !isVideo || !LocalAnimatedVideoThumbnails.current
    if (static) {
        ImageThumbnail(
            modifier,
            respectNetworkCacheHeaders,
            url,
            onSuccess,
            contentScale,
            bg,
            contentDescription
        )
    } else {
        Animated(
            url,
            modifier,
            aspectRatio,
            contentScale,
            respectNetworkCacheHeaders,
            onSuccess,
            bg,
            contentDescription
        )
    }
}

@Composable
private fun Animated(
    url: String?,
    modifier: Modifier,
    aspectRatio: Float?,
    contentScale: ContentScale,
    respectNetworkCacheHeaders: Boolean,
    onSuccess: () -> Unit,
    bg: ColorPainter?,
    contentDescription: String?
) {
    var loaded by remember(url) {
        mutableStateOf(false)
    }
    var error by remember(url) {
        mutableStateOf(false)
    }
    val headersUseCase = LocalAuthenticationHeaders.current
    val headers = remember(url) {
        url?.let {
            headersUseCase.headers(url).toMap()
        } ?: emptyMap()
    }
    if (!error) {
        AndroidView(
            factory = { context ->
                TextureVideoView(context)
            },
            modifier = modifier
                .run {
                    aspectRatio?.let { aspectRatio(it) } ?: this
                },
            onReset = { },
            update = {
                it.setScaleType(
                    when (contentScale) {
                        ContentScale.Crop -> ScaleType.CENTER_CROP
                        else -> ScaleType.FIT
                    }
                )
                if (url != null) {
                    it.setListener(
                        onPrepared = { it.startPlayback() },
                        onEnd = { it.startPlayback() },
                        onError = {
                            error = true
                            loaded = false
                        }
                    )
                    it.playUrl(url, headers)
                    CoroutineScope(Dispatchers.Main).launch {
                        while (!loaded) {
                            val mp = it.mediaPlayer
                            if (mp != null && mp.isPlaying && mp.currentPosition > 0) {
                                loaded = true
                            } else {
                                delay(200)
                            }
                        }
                    }
                }
            },
            onRelease = { },
        )
    }
    if (!loaded) {
        ImageThumbnail(
            modifier,
            respectNetworkCacheHeaders,
            url,
            onSuccess,
            contentScale,
            bg,
            contentDescription
        )
    }
}

@Composable
private fun ImageThumbnail(
    modifier: Modifier,
    respectNetworkCacheHeaders: Boolean,
    url: String?,
    onSuccess: () -> Unit,
    contentScale: ContentScale,
    bg: ColorPainter?,
    contentDescription: String?
) {
    var size by remember {
        mutableStateOf(null as IntSize?)
    }
    AsyncImage(
        modifier = modifier.onGloballyPositioned {
            size = it.size
        },
        imageLoader = if (respectNetworkCacheHeaders)
            LocalThumbnailWithNetworkCacheImageLoader.current
        else
            LocalThumbnailImageLoader.current,
        model = url.toRequest(
            precision = Precision.INEXACT,
            size = size?.let { Size(it.width, it.height) }
        ) {
            onSuccess()
        },
        contentScale = contentScale,
        placeholder = bg,
        error = bg,
        fallback = bg,
        contentDescription = contentDescription,
    )
}