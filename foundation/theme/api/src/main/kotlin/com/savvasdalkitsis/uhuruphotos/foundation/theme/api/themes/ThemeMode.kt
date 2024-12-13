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
package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable

enum class ThemeMode(
    val friendlyName: String,
    @DrawableRes val icon: Int,
) {

    FOLLOW_SYSTEM("Follow system", drawable.ic_brightness_auto),
    DARK_MODE("Dark mode", drawable.ic_dark_mode),
    LIGHT_MODE("Light mode", drawable.ic_light_mode);

    @Composable
    fun isDark(): Boolean = when (this) {
        FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DARK_MODE -> true
        LIGHT_MODE -> false
    }

    companion object {
        val default = FOLLOW_SYSTEM
    }
}