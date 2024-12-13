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