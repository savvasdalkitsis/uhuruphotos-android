package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

object DynamicTheme : Theme {

    override val label: Int = string.dynamic

    @Composable
    @RequiresApi(Build.VERSION_CODES.S)
    override fun getColorScheme(themeMode: ThemeMode, themeContrast: ThemeContrast): ColorScheme =
        with(LocalContext.current) {
            if (themeMode.isDark()) dynamicDarkColorScheme(this) else dynamicLightColorScheme(this)
        }
}