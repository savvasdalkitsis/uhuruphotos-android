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

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.HIGH
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.MEDIUM
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.NORMAL
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.LIGHT_MODE

data object BlueTheme : StaticTheme() {

    override val label = string.blue

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF415F91),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFD6E3FF),
        onPrimaryContainer = Color(0xFF001B3E),
        secondary = Color(0xFF565F71),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFDAE2F9),
        onSecondaryContainer = Color(0xFF131C2B),
        tertiary = Color(0xFF705575),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFAD8FD),
        onTertiaryContainer = Color(0xFF28132E),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFF9F9FF),
        onBackground = Color(0xFF191C20),
        surface = Color(0xFFF9F9FF),
        onSurface = Color(0xFF191C20),
        surfaceVariant = Color(0xFFE0E2EC),
        onSurfaceVariant = Color(0xFF44474E),
        outline = Color(0xFF74777F),
        outlineVariant = Color(0xFFC4C6D0),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E3036),
        inverseOnSurface = Color(0xFFF0F0F7),
        inversePrimary = Color(0xFFAAC7FF),
        surfaceDim = Color(0xFFD9D9E0),
        surfaceBright = Color(0xFFF9F9FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF3F3FA),
        surfaceContainer = Color(0xFFEDEDF4),
        surfaceContainerHigh = Color(0xFFE7E8EE),
        surfaceContainerHighest = Color(0xFFE2E2E9),
    )

    override val darkScheme = darkColorScheme(
        primary = Color(0xFFAAC7FF),
        onPrimary = Color(0xFF0A305F),
        primaryContainer = Color(0xFF284777),
        onPrimaryContainer = Color(0xFFD6E3FF),
        secondary = Color(0xFFBEC6DC),
        onSecondary = Color(0xFF283141),
        secondaryContainer = Color(0xFF3E4759),
        onSecondaryContainer = Color(0xFFDAE2F9),
        tertiary = Color(0xFFDDBCE0),
        onTertiary = Color(0xFF3F2844),
        tertiaryContainer = Color(0xFF573E5C),
        onTertiaryContainer = Color(0xFFFAD8FD),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF111318),
        onBackground = Color(0xFFE2E2E9),
        surface = Color(0xFF111318),
        onSurface = Color(0xFFE2E2E9),
        surfaceVariant = Color(0xFF44474E),
        onSurfaceVariant = Color(0xFFC4C6D0),
        outline = Color(0xFF8E9099),
        outlineVariant = Color(0xFF44474E),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE2E2E9),
        inverseOnSurface = Color(0xFF2E3036),
        inversePrimary = Color(0xFF415F91),
        surfaceDim = Color(0xFF111318),
        surfaceBright = Color(0xFF37393E),
        surfaceContainerLowest = Color(0xFF0C0E13),
        surfaceContainerLow = Color(0xFF191C20),
        surfaceContainer = Color(0xFF1D2024),
        surfaceContainerHigh = Color(0xFF282A2F),
        surfaceContainerHighest = Color(0xFF33353A),
    )

    override val mediumContrastLightColorScheme = lightColorScheme(
        primary = Color(0xFF234373),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF5875A8),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF3A4354),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF6C7588),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF523A58),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF876B8C),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFF8C0009),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFDA342E),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFF9F9FF),
        onBackground = Color(0xFF191C20),
        surface = Color(0xFFF9F9FF),
        onSurface = Color(0xFF191C20),
        surfaceVariant = Color(0xFFE0E2EC),
        onSurfaceVariant = Color(0xFF40434A),
        outline = Color(0xFF5C5F67),
        outlineVariant = Color(0xFF787A83),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E3036),
        inverseOnSurface = Color(0xFFF0F0F7),
        inversePrimary = Color(0xFFAAC7FF),
        surfaceDim = Color(0xFFD9D9E0),
        surfaceBright = Color(0xFFF9F9FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF3F3FA),
        surfaceContainer = Color(0xFFEDEDF4),
        surfaceContainerHigh = Color(0xFFE7E8EE),
        surfaceContainerHighest = Color(0xFFE2E2E9),
    )

    override val highContrastLightColorScheme = lightColorScheme(
        primary = Color(0xFF00214A),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF234373),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF192232),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF3A4354),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF301A35),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF523A58),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFF4E0002),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF8C0009),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFF9F9FF),
        onBackground = Color(0xFF191C20),
        surface = Color(0xFFF9F9FF),
        onSurface = Color(0xFF000000),
        surfaceVariant = Color(0xFFE0E2EC),
        onSurfaceVariant = Color(0xFF21242B),
        outline = Color(0xFF40434A),
        outlineVariant = Color(0xFF40434A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E3036),
        inverseOnSurface = Color(0xFFFFFFFF),
        inversePrimary = Color(0xFFE5ECFF),
        surfaceDim = Color(0xFFD9D9E0),
        surfaceBright = Color(0xFFF9F9FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF3F3FA),
        surfaceContainer = Color(0xFFEDEDF4),
        surfaceContainerHigh = Color(0xFFE7E8EE),
        surfaceContainerHighest = Color(0xFFE2E2E9),
    )

    override val mediumContrastDarkColorScheme = darkColorScheme(
        primary = Color(0xFFB1CBFF),
        onPrimary = Color(0xFF001634),
        primaryContainer = Color(0xFF7491C7),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFC2CBE0),
        onSecondary = Color(0xFF0D1626),
        secondaryContainer = Color(0xFF8891A5),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFE1C0E5),
        onTertiary = Color(0xFF230E29),
        tertiaryContainer = Color(0xFFA487A9),
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFFFBAB1),
        onError = Color(0xFF370001),
        errorContainer = Color(0xFFFF5449),
        onErrorContainer = Color(0xFF000000),
        background = Color(0xFF111318),
        onBackground = Color(0xFFE2E2E9),
        surface = Color(0xFF111318),
        onSurface = Color(0xFFFBFAFF),
        surfaceVariant = Color(0xFF44474E),
        onSurfaceVariant = Color(0xFFC8CAD4),
        outline = Color(0xFFA0A3AC),
        outlineVariant = Color(0xFF80838C),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE2E2E9),
        inverseOnSurface = Color(0xFF282A2F),
        inversePrimary = Color(0xFF294878),
        surfaceDim = Color(0xFF111318),
        surfaceBright = Color(0xFF37393E),
        surfaceContainerLowest = Color(0xFF0C0E13),
        surfaceContainerLow = Color(0xFF191C20),
        surfaceContainer = Color(0xFF1D2024),
        surfaceContainerHigh = Color(0xFF282A2F),
        surfaceContainerHighest = Color(0xFF33353A),
    )

    override val highContrastDarkColorScheme = darkColorScheme(
        primary = Color(0xFFFBFAFF),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFFB1CBFF),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFFBFAFF),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFFC2CBE0),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFFFF9FA),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFFE1C0E5),
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFFFF9F9),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFFFFBAB1),
        onErrorContainer = Color(0xFF000000),
        background = Color(0xFF111318),
        onBackground = Color(0xFFE2E2E9),
        surface = Color(0xFF111318),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF44474E),
        onSurfaceVariant = Color(0xFFFBFAFF),
        outline = Color(0xFFC8CAD4),
        outlineVariant = Color(0xFFC8CAD4),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE2E2E9),
        inverseOnSurface = Color(0xFF000000),
        inversePrimary = Color(0xFF002959),
        surfaceDim = Color(0xFF111318),
        surfaceBright = Color(0xFF37393E),
        surfaceContainerLowest = Color(0xFF0C0E13),
        surfaceContainerLow = Color(0xFF191C20),
        surfaceContainer = Color(0xFF1D2024),
        surfaceContainerHigh = Color(0xFF282A2F),
        surfaceContainerHighest = Color(0xFF33353A),
    )
}

@Preview
@Composable
private fun PreviewThemeNormal() {
    PreviewTheme(BlueTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeMedium() {
    PreviewTheme(BlueTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeHome() {
    PreviewTheme(BlueTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeDark() {
    PreviewTheme(BlueTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeDarkMedium() {
    PreviewTheme(BlueTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeDarkHome() {
    PreviewTheme(BlueTheme, DARK_MODE, HIGH)
}