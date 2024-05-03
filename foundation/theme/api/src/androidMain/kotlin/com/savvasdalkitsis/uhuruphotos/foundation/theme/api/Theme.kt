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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.extensions.setHDR
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalSystemUiController

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
