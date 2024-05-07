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

import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import crocodile8008.videoviewcache.lib.playUrl
import crocodile8008.videoviewcache.lib.stop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
actual fun Thumbnail(
    modifier: Modifier,
    url: String?,
    contentScale: ContentScale,
    placeholder: Int?,
    contentDescription: String?,
    respectNetworkCacheHeaders: Boolean,
    isVideo: Boolean,
    onSuccess: () -> Unit,
) {
    val bg = remember(placeholder) {
        placeholder?.let { ColorPainter(Color(it)) }
    }
    if (!isVideo || !LocalAnimatedVideoThumbnails.current) {
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
        var loaded by remember(url) {
            mutableStateOf(false)
        }
        var error by remember(url) {
            mutableStateOf(false)
        }
        if (!error) {
            AndroidView(
                factory = { context ->
                    VideoView(context)
                },
                modifier = modifier,
                onReset = { it.stop() },
                update = {
                    if (url != null) {
                        with(it) {
                            playUrl(url)
                            setOnPreparedListener { start() }
                            setOnCompletionListener { start() }
                            setOnErrorListener { _, _, _ ->
                                error = true
                                true
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                while (!loaded) {
                                    if (isPlaying && currentPosition > 0) {
                                        loaded = true
                                    } else {
                                        delay(200)
                                    }
                                }
                            }
                        }
                    }
                },
                onRelease = { it.suspend() },
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
    AsyncImage(
        modifier = modifier,
        imageLoader = if (respectNetworkCacheHeaders)
            LocalThumbnailWithNetworkCacheImageLoader.current
        else
            LocalThumbnailImageLoader.current,
        model = url?.toRequest {
            onSuccess()
        },
        contentScale = contentScale,
        placeholder = bg,
        error = bg,
        fallback = bg,
        contentDescription = contentDescription,
    )
}