/*
Copyright 2025 Savvas Dalkitsis

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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.foundation.theme.api

import android.graphics.Color.CYAN
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.BlueTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.Theme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize

data class PreviewThemeData(
    val themeMode: ThemeMode,
    val themeVariant: ThemeVariant,
)

class PreviewThemeDataProvider : PreviewParameterProvider<PreviewThemeData> {
    override val values: Sequence<PreviewThemeData> =
        ThemeMode.entries.flatMap { themeMode ->
            ThemeVariant.entries.flatMap { themeVariant ->
                listOf(
                    PreviewThemeData(themeMode, themeVariant),
                    PreviewThemeData(themeMode, themeVariant),
                    PreviewThemeData(themeMode, themeVariant),
                    PreviewThemeData(themeMode, themeVariant),
                )
            }
        }.asSequence()
}

@Composable
fun PreviewAppTheme(
    themeData: PreviewThemeData,
    themeContrast: ThemeContrast = ThemeContrast.NORMAL,
    content: @Composable SharedTransitionScope.() -> Unit,
) {
    PreviewAppTheme(
        themeMode = themeData.themeMode,
        themeContrast = themeContrast,
        theme = themeData.themeVariant,
        content = content
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PreviewAppTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT_MODE,
    themeContrast: ThemeContrast = ThemeContrast.NORMAL,
    theme: Theme = BlueTheme,
    content: @Composable SharedTransitionScope.() -> Unit,
) {
    val drawable = CYAN.toDrawable()
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .placeholder(drawable)
        .error(drawable)
        .fallback(drawable)
        .build()
    CompositionLocalProvider(
        LocalSystemUiController provides NoOpSystemUiController,
        LocalThemeMode provides themeMode,
        LocalThemeContrast provides themeContrast,
        LocalTheme provides theme,
        LocalWindowSize provides WindowSizeClass.calculateFromSize(DpSize(450.dp, 800.dp)),
        LocalThumbnailImageLoader provides imageLoader,
        LocalThumbnailWithNetworkCacheImageLoader provides imageLoader,
        LocalFullImageLoader provides imageLoader,
        LocalThumbnailImageLoader provides ImageLoader(LocalContext.current)
    ) {
        AppTheme(themeMode = themeMode) {
            Surface {
                SharedTransitionLayout {
                    Box {
                        content()
                    }
                }
            }
        }
    }
}
