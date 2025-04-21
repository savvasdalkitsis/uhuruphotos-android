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
package com.savvasdalkitsis.uhuruphotos.foundation.theme.api

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.Theme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode


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
        ThemeLoadSideEffect(themeMode)
        content()
    }
}