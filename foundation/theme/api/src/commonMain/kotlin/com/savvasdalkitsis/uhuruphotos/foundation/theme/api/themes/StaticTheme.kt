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
package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.HIGH
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.MEDIUM
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.NORMAL

sealed class StaticTheme : Theme {

    abstract val darkScheme: ColorScheme
    abstract val mediumContrastDarkColorScheme: ColorScheme
    abstract val highContrastDarkColorScheme: ColorScheme
    abstract val lightScheme: ColorScheme
    abstract val mediumContrastLightColorScheme: ColorScheme
    abstract val highContrastLightColorScheme: ColorScheme

    @Composable
    override fun getColorScheme(themeMode: ThemeMode, themeContrast: ThemeContrast): ColorScheme =
        when {
            themeMode.isDark() -> when (themeContrast) {
                NORMAL -> darkScheme
                MEDIUM -> mediumContrastDarkColorScheme
                HIGH -> highContrastDarkColorScheme
            }
            else -> when (themeContrast) {
                NORMAL -> lightScheme
                MEDIUM -> mediumContrastLightColorScheme
                HIGH -> highContrastLightColorScheme
            }
        }

}