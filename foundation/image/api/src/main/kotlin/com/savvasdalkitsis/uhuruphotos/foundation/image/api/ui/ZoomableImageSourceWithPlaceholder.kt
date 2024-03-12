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
package com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import kotlinx.coroutines.flow.Flow
import me.saket.telephoto.zoomable.ZoomableImageSource
import me.saket.telephoto.zoomable.ZoomableImageSource.PainterDelegate
import me.saket.telephoto.zoomable.ZoomableImageSource.ResolveResult
import me.saket.telephoto.zoomable.ZoomableImageSource.SubSamplingDelegate
import me.saket.telephoto.zoomable.coil.coil
import kotlin.time.Duration

@Composable
fun zoomableImageSourceWithPlaceholder(
    placeholderUrl: String?,
    fullResRequest: ImageRequest.Builder,
    thumbImageLoader: ImageLoader = LocalThumbnailImageLoader.current,
    fullImageLoader: ImageLoader = LocalFullImageLoader.current,
) : ZoomableImageSource {
    var placeholder by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    val source = ZoomableImageSource.coil(fullResRequest.crossfade(false).build(), fullImageLoader)
    val context = LocalContext.current

    LaunchedEffect(placeholderUrl) {
        thumbImageLoader.execute(
            ImageRequest.Builder(context)
            .data(placeholderUrl)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .target(onSuccess = { placeholder = it.toBitmap().asImageBitmap() })
            .build()
        )
    }
    return remember(placeholder) {
        val bitmapPainter = placeholder?.let { BitmapPainter(it) }
        object : ZoomableImageSource {
            @Composable
            override fun resolve(canvasSize: Flow<Size>): ResolveResult {
                val result = key(source) {
                    source.resolve(canvasSize).copy().takeIf {
                        when (val d = it.delegate) {
                            null -> false
                            is PainterDelegate -> d.painter != null
                            is SubSamplingDelegate -> true
                        }
                    }
                }
                return result ?: ResolveResult(
                    placeholder = null,
                    delegate = PainterDelegate(
                        painter = bitmapPainter,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ResolveResult.copy() = remember(this) {
    ResolveResult(
        placeholder = placeholder,
        delegate = delegate,
        crossfadeDuration = Duration.ZERO,
    )
}