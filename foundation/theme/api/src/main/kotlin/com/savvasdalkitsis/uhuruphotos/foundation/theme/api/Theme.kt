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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.extensions.setHDR
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize

private val DarkColorPalette = darkColors(
    primary = Color.White,
    primaryVariant = Color.White,
    secondary = Color.White,
    secondaryVariant = Color(56, 56, 56, 255),
    background = Color.Black,
    surface = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = Color.Black,
    primaryVariant = Color.Black,
    secondary = Color.Black,
    secondaryVariant = Color(231, 231, 231, 255),
    background = Color.White,
    surface = Color.White,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Suppress("MagicNumber")
data object CustomColors {
    val syncError = Color(158, 6, 37)
    val syncSuccess = Color(21, 158, 6, 255)
    val syncQueued = Color(33, 150, 243, 255)
    val selected = Color(69, 158, 59, 255)
    val alert = Color(255, 152, 0, 255)
    val selectedBackground = selected.copy(alpha = 0.2f)
    val emptyItem: Color
        @Composable get() = if (MaterialTheme.colors.isLight) {
            Color.LightGray
        } else {
            Color.DarkGray
        }
}

@Composable
fun ContentTheme(
    theme: ThemeMode = LocalThemeMode.current,
    content: @Composable () -> Unit
) {
    val isDark = when (theme) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK_MODE -> true
        ThemeMode.LIGHT_MODE -> false
    }
    val colors = if (isDark) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
    ) {
        content()
    }
}

@Composable
fun AppTheme(
    theme: ThemeMode = LocalThemeMode.current,
    content: @Composable () -> Unit
) {
    ContentTheme(theme) {
        val isLight = MaterialTheme.colors.isLight
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
        LocalWindowSize provides WindowSizeClass.calculateFromSize(DpSize(450.dp, 800.dp)),
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