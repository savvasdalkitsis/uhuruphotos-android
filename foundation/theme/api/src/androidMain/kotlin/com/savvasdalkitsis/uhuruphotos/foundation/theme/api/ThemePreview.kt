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
package com.savvasdalkitsis.uhuruphotos.foundation.theme.api

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Surface
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize

@Composable
fun PreviewAppTheme(
    theme: ThemeMode = ThemeMode.LIGHT_MODE,
    content: @Composable BoxScope.() -> Unit,
) {
    val drawable = ColorDrawable(android.graphics.Color.CYAN)
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .placeholder(drawable)
        .error(drawable)
        .fallback(drawable)
        .build()
    CompositionLocalProvider(
        LocalSystemUiController provides NoOpSystemUiController,
        LocalWindowSize provides calculateWindowSizeClass(),
        LocalThumbnailImageLoader provides imageLoader,
        LocalThumbnailWithNetworkCacheImageLoader provides imageLoader,
        LocalFullImageLoader provides imageLoader,
    ) {
        AppTheme(theme = theme) {
            Surface {
                Box {
                    content()
                }
            }
        }
    }
}