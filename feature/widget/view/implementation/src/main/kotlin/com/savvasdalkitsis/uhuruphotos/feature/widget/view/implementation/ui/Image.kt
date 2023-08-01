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
package com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.layout.ContentScale
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.widget.view.implementation.R

@Composable
fun Image(
    modifier: GlanceModifier = GlanceModifier,
    contentScale: ContentScale,
    url: String?,
    onClick: () -> Unit = {},
) {
    val loader = LocalThumbnailWithNetworkCacheImageLoader.current
    val context = LocalContext.current
    var image by remember(url) {
        mutableStateOf<Bitmap?>(null)
    }
    androidx.glance.Image(
        modifier = modifier.clickable(onClick),
        contentScale = contentScale,
        provider = when (val bitmap = image) {
            null -> ImageProvider(R.drawable.placeholder)
            else -> ImageProvider(bitmap)
        },
        contentDescription = null
    )
    LaunchedEffect(url) {
        if (url != null) {
            loader.enqueue(ImageRequest.Builder(context)
                .data(url)
                .allowConversionToBitmap(true)
                .allowHardware(true)
                .transformations(object :Transformation {
                    override val cacheKey: String get() = "$url::scaled"

                    override suspend fun transform(input: Bitmap, size: Size): Bitmap =
                        input.scale(input.width / 3, input.height / 3)
                })
                .target { image = (it as BitmapDrawable).bitmap }
                .build())
        }
    }
}