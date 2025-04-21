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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_brightness_auto
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_dark_mode
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_light_mode

enum class ThemeMode(
    val friendlyName: String,
    val icon: DrawableResource,
) {

    FOLLOW_SYSTEM("Follow system", Res.drawable.ic_brightness_auto),
    DARK_MODE("Dark mode", Res.drawable.ic_dark_mode),
    LIGHT_MODE("Light mode", Res.drawable.ic_light_mode);

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