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
package com.savvasdalkitsis.uhuruphotos.api.image.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun Image(
    modifier: Modifier = Modifier,
    url: String?,
    contentScale: ContentScale,
    placeholder: Painter? = null,
    contentDescription: String?,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .diskCachePolicy(url.diskCachePolicy())
            .build(),
        contentScale = contentScale,
        placeholder = placeholder,
        contentDescription = contentDescription,
    )
}

@Composable
fun Image(
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
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                url = lowResUrl,
                contentScale = contentScale,
                placeholder = placeholder,
                contentDescription = null,
            )
        }
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullResUrl)
                .size(Size.ORIGINAL)
                .diskCachePolicy(fullResUrl.diskCachePolicy())
                .listener(onSuccess = { _, _ ->
                    showLowRes = false
                    onFullResImageLoaded()
                })
                .build(),
            contentScale = contentScale,
            contentDescription = contentDescription,
        )
    }

}

private fun String?.diskCachePolicy(): CachePolicy = if (orEmpty().startsWith("content://")) {
    CachePolicy.DISABLED
} else {
    CachePolicy.ENABLED
}
