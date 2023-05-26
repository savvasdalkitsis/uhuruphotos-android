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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.NoOpSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.Theme.Dark
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.Theme.Light
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalSystemUiController

private val DarkColorPalette = darkColorScheme(
    primary = Color.White,
    secondary = Color.White,
    background = Color.Black,
    surface = Color.Black,
)

private val LightColorPalette = lightColorScheme(
    primary = Color.Black,
    secondary = Color.Black,
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
    val selected = Color(69, 158, 59, 255)
    val emptyItem: Color
        @Composable get() = if (LocalTheme.current == Light) {
            Color.LightGray
        } else {
            Color.DarkGray
        }
}

enum class Theme {
    Dark, Light
}

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    CompositionLocalProvider(
        LocalTheme provides if (darkTheme) Dark else Light
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            shapes = Shapes,
        ) {
            val systemUiController = LocalSystemUiController.current
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !darkTheme
                )
            }
            content()
        }
    }
}

@Composable
fun PreviewAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSystemUiController provides NoOpSystemUiController
    ) {
        AppTheme(darkTheme = darkTheme) {
            Surface {
                content()
            }
        }
    }
}