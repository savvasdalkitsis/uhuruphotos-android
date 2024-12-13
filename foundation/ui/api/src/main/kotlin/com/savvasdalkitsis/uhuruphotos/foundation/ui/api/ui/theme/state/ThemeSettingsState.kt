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

package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.Theme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape

@Immutable
data class ThemeSettingsState(
    val themeMode: ThemeMode = ThemeMode.LIGHT_MODE,
    val theme: Theme = ThemeVariant.default,
    val themeContrast: ThemeContrast = ThemeContrast.default,
    val collageSpacing: Int = 0,
    val collageSpacingIncludeEdges: Boolean = false,
    val collageShape: CollageShape = CollageShape.default,
)