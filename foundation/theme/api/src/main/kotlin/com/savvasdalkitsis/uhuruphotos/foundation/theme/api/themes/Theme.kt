package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

interface Theme {

    val label: Int
    @Composable
    fun getColorScheme(themeMode: ThemeMode, themeContrast: ThemeContrast): ColorScheme

    companion object {
        val default = BlueTheme
    }
}