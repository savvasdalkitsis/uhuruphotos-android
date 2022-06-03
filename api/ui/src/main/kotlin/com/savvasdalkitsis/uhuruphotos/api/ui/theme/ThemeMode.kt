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
package com.savvasdalkitsis.uhuruphotos.api.ui.theme

import androidx.annotation.DrawableRes
import com.savvasdalkitsis.uhuruphotos.api.icons.R

enum class ThemeMode(
    val friendlyName: String,
    @DrawableRes val icon: Int,
) {

    FOLLOW_SYSTEM("Follow system", R.drawable.ic_brightness_auto),
    DARK_MODE("Dark mode", R.drawable.ic_dark_mode),
    LIGHT_MODE("Light mode", R.drawable.ic_light_mode);

    companion object {
        val default = FOLLOW_SYSTEM
    }
}