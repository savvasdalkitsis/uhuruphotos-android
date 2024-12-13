package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.state

import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.Theme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape

data class ThemeSettingsState(
    val themeMode: ThemeMode = ThemeMode.LIGHT_MODE,
    val theme: Theme = ThemeVariant.default,
    val themeContrast: ThemeContrast = ThemeContrast.default,
    val collageSpacing: Int = 0,
    val collageSpacingIncludeEdges: Boolean = false,
    val collageShape: CollageShape = CollageShape.default,
)