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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.extensions.setHDR
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

@Suppress("MagicNumber")
data object CustomColors {
    val syncError = Color(158, 6, 37)
    val syncSuccess = Color(21, 158, 6, 255)
    val syncQueued = Color(33, 150, 243, 255)
    val selected = Color(69, 158, 59, 255)
    val alert = Color(255, 152, 0, 255)
    val selectedBackground = selected.copy(alpha = 0.2f)
    val emptyItem: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
}

@Composable
fun ContentTheme(
    themeMode: ThemeMode = LocalThemeMode.current,
    themeContrast: ThemeContrast = LocalThemeContrast.current,
    theme: Theme = LocalTheme.current,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = theme.getColorScheme(themeMode, themeContrast),
        typography = AppTypography,
        content = content
    )
}

@Composable
fun AppTheme(
    themeMode: ThemeMode = LocalThemeMode.current,
    content: @Composable () -> Unit
) {
    ContentTheme(themeMode) {
        val isLight = !themeMode.isDark()
        val systemUiController = LocalSystemUiController.current
        val context = LocalContext.current
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = isLight
            )
            context.setHDR(false)
        }
        content()
    }
}


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
    content: @Composable BoxScope.() -> Unit,
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
                Box {
                    content()
                }
            }
        }
    }
}

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)