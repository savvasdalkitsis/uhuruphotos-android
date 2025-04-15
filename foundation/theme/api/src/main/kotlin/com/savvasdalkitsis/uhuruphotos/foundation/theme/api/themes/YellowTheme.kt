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
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.HIGH
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.MEDIUM
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.NORMAL
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.LIGHT_MODE
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.yellow

data object YellowTheme : StaticTheme() {

    override val label = string.yellow

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF6D5E0F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFF8E287),
        onPrimaryContainer = Color(0xFF221B00),
        secondary = Color(0xFF665E40),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFEEE2BC),
        onSecondaryContainer = Color(0xFF211B04),
        tertiary = Color(0xFF43664E),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFC5ECCE),
        onTertiaryContainer = Color(0xFF00210F),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFFFF9EE),
        onBackground = Color(0xFF1E1B13),
        surface = Color(0xFFFFF9EE),
        onSurface = Color(0xFF1E1B13),
        surfaceVariant = Color(0xFFEAE2D0),
        onSurfaceVariant = Color(0xFF4B4739),
        outline = Color(0xFF7C7767),
        outlineVariant = Color(0xFFCDC6B4),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF333027),
        inverseOnSurface = Color(0xFFF7F0E2),
        inversePrimary = Color(0xFFDBC66E),
        surfaceDim = Color(0xFFE0D9CC),
        surfaceBright = Color(0xFFFFF9EE),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFAF3E5),
        surfaceContainer = Color(0xFFF4EDDF),
        surfaceContainerHigh = Color(0xFFEEE8DA),
        surfaceContainerHighest = Color(0xFFE8E2D4),
    )

    override val darkScheme = darkColorScheme(
        primary = Color(0xFFDBC66E),
        onPrimary = Color(0xFF3A3000),
        primaryContainer = Color(0xFF534600),
        onPrimaryContainer = Color(0xFFF8E287),
        secondary = Color(0xFFD1C6A1),
        onSecondary = Color(0xFF363016),
        secondaryContainer = Color(0xFF4E472A),
        onSecondaryContainer = Color(0xFFEEE2BC),
        tertiary = Color(0xFFA9D0B3),
        onTertiary = Color(0xFF143723),
        tertiaryContainer = Color(0xFF2C4E38),
        onTertiaryContainer = Color(0xFFC5ECCE),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF15130B),
        onBackground = Color(0xFFE8E2D4),
        surface = Color(0xFF15130B),
        onSurface = Color(0xFFE8E2D4),
        surfaceVariant = Color(0xFF4B4739),
        onSurfaceVariant = Color(0xFFCDC6B4),
        outline = Color(0xFF969080),
        outlineVariant = Color(0xFF4B4739),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE8E2D4),
        inverseOnSurface = Color(0xFF333027),
        inversePrimary = Color(0xFF6D5E0F),
        surfaceDim = Color(0xFF15130B),
        surfaceBright = Color(0xFF3C3930),
        surfaceContainerLowest = Color(0xFF100E07),
        surfaceContainerLow = Color(0xFF1E1B13),
        surfaceContainer = Color(0xFF222017),
        surfaceContainerHigh = Color(0xFF2D2A21),
        surfaceContainerHighest = Color(0xFF38352B),
    )

    override  val mediumContrastLightColorScheme = lightColorScheme(
        primary = Color(0xFF4F4200),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF857425),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF4A4327),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF7D7455),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF284A34),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF597D64),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFF8C0009),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFDA342E),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFFFF9EE),
        onBackground = Color(0xFF1E1B13),
        surface = Color(0xFFFFF9EE),
        onSurface = Color(0xFF1E1B13),
        surfaceVariant = Color(0xFFEAE2D0),
        onSurfaceVariant = Color(0xFF474335),
        outline = Color(0xFF645F50),
        outlineVariant = Color(0xFF807A6B),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF333027),
        inverseOnSurface = Color(0xFFF7F0E2),
        inversePrimary = Color(0xFFDBC66E),
        surfaceDim = Color(0xFFE0D9CC),
        surfaceBright = Color(0xFFFFF9EE),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFAF3E5),
        surfaceContainer = Color(0xFFF4EDDF),
        surfaceContainerHigh = Color(0xFFEEE8DA),
        surfaceContainerHighest = Color(0xFFE8E2D4),
    )

    override val highContrastLightColorScheme = lightColorScheme(
        primary = Color(0xFF292200),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF4F4200),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF282209),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF4A4327),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF042815),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF284A34),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFF4E0002),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF8C0009),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFFFF9EE),
        onBackground = Color(0xFF1E1B13),
        surface = Color(0xFFFFF9EE),
        onSurface = Color(0xFF000000),
        surfaceVariant = Color(0xFFEAE2D0),
        onSurfaceVariant = Color(0xFF272418),
        outline = Color(0xFF474335),
        outlineVariant = Color(0xFF474335),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF333027),
        inverseOnSurface = Color(0xFFFFFFFF),
        inversePrimary = Color(0xFFFFECA2),
        surfaceDim = Color(0xFFE0D9CC),
        surfaceBright = Color(0xFFFFF9EE),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFAF3E5),
        surfaceContainer = Color(0xFFF4EDDF),
        surfaceContainerHigh = Color(0xFFEEE8DA),
        surfaceContainerHighest = Color(0xFFE8E2D4),
    )

    override val mediumContrastDarkColorScheme = darkColorScheme(
        primary = Color(0xFFE0CA72),
        onPrimary = Color(0xFF1C1600),
        primaryContainer = Color(0xFFA3903F),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFD6CAA5),
        onSecondary = Color(0xFF1B1602),
        secondaryContainer = Color(0xFF9A916F),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFADD4B7),
        onTertiary = Color(0xFF001B0C),
        tertiaryContainer = Color(0xFF75997F),
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFFFBAB1),
        onError = Color(0xFF370001),
        errorContainer = Color(0xFFFF5449),
        onErrorContainer = Color(0xFF000000),
        background = Color(0xFF15130B),
        onBackground = Color(0xFFE8E2D4),
        surface = Color(0xFF15130B),
        onSurface = Color(0xFFFFFAF5),
        surfaceVariant = Color(0xFF4B4739),
        onSurfaceVariant = Color(0xFFD1CAB8),
        outline = Color(0xFFA9A292),
        outlineVariant = Color(0xFF888373),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE8E2D4),
        inverseOnSurface = Color(0xFF2D2A21),
        inversePrimary = Color(0xFF554700),
        surfaceDim = Color(0xFF15130B),
        surfaceBright = Color(0xFF3C3930),
        surfaceContainerLowest = Color(0xFF100E07),
        surfaceContainerLow = Color(0xFF1E1B13),
        surfaceContainer = Color(0xFF222017),
        surfaceContainerHigh = Color(0xFF2D2A21),
        surfaceContainerHighest = Color(0xFF38352B),
    )

    override val highContrastDarkColorScheme = darkColorScheme(
        primary = Color(0xFFFFFAF5),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFFE0CA72),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFFFFAF5),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFFD6CAA5),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFEFFFF0),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFFADD4B7),
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFFFF9F9),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFFFFBAB1),
        onErrorContainer = Color(0xFF000000),
        background = Color(0xFF15130B),
        onBackground = Color(0xFFE8E2D4),
        surface = Color(0xFF15130B),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF4B4739),
        onSurfaceVariant = Color(0xFFFFFAF5),
        outline = Color(0xFFD1CAB8),
        outlineVariant = Color(0xFFD1CAB8),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE8E2D4),
        inverseOnSurface = Color(0xFF000000),
        inversePrimary = Color(0xFF322A00),
        surfaceDim = Color(0xFF15130B),
        surfaceBright = Color(0xFF3C3930),
        surfaceContainerLowest = Color(0xFF100E07),
        surfaceContainerLow = Color(0xFF1E1B13),
        surfaceContainer = Color(0xFF222017),
        surfaceContainerHigh = Color(0xFF2D2A21),
        surfaceContainerHighest = Color(0xFF38352B),
    )
}

@Preview
@Composable
private fun PreviewThemeNormal() {
    PreviewTheme(YellowTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeMedium() {
    PreviewTheme(YellowTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeHome() {
    PreviewTheme(YellowTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeDark() {
    PreviewTheme(YellowTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeDarkMedium() {
    PreviewTheme(YellowTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeDarkHome() {
    PreviewTheme(YellowTheme, DARK_MODE, HIGH)
}