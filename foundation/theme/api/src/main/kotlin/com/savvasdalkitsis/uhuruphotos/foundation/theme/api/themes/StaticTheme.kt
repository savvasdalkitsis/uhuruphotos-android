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