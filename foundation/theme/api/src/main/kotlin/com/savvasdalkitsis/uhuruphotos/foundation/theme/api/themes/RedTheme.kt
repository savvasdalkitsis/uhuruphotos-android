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
import uhuruphotos_android.foundation.strings.api.generated.resources.red

data object RedTheme : StaticTheme() {

    override val label = string.red

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF8F4C36),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDBD0),
        onPrimaryContainer = Color(0xFF3A0B00),
        secondary = Color(0xFF77574D),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFDBD0),
        onSecondaryContainer = Color(0xFF2C160E),
        tertiary = Color(0xFF6B5E2F),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFF4E2A7),
        onTertiaryContainer = Color(0xFF221B00),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFFFF8F6),
        onBackground = Color(0xFF231917),
        surface = Color(0xFFFFF8F6),
        onSurface = Color(0xFF231917),
        surfaceVariant = Color(0xFFF5DED7),
        onSurfaceVariant = Color(0xFF53433F),
        outline = Color(0xFF85736E),
        outlineVariant = Color(0xFFD8C2BC),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF392E2B),
        inverseOnSurface = Color(0xFFFFEDE8),
        inversePrimary = Color(0xFFFFB59E),
        surfaceDim = Color(0xFFE8D6D1),
        surfaceBright = Color(0xFFFFF8F6),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFFF1ED),
        surfaceContainer = Color(0xFFFCEAE5),
        surfaceContainerHigh = Color(0xFFF7E4DF),
        surfaceContainerHighest = Color(0xFFF1DFDA),
    )

    override val darkScheme = darkColorScheme(
        primary = Color(0xFFFFB59E),
        onPrimary = Color(0xFF561F0D),
        primaryContainer = Color(0xFF723521),
        onPrimaryContainer = Color(0xFFFFDBD0),
        secondary = Color(0xFFE7BDB1),
        onSecondary = Color(0xFF442A22),
        secondaryContainer = Color(0xFF5D4037),
        onSecondaryContainer = Color(0xFFFFDBD0),
        tertiary = Color(0xFFD7C68D),
        onTertiary = Color(0xFF3A3005),
        tertiaryContainer = Color(0xFF52461A),
        onTertiaryContainer = Color(0xFFF4E2A7),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF1A110F),
        onBackground = Color(0xFFF1DFDA),
        surface = Color(0xFF1A110F),
        onSurface = Color(0xFFF1DFDA),
        surfaceVariant = Color(0xFF53433F),
        onSurfaceVariant = Color(0xFFD8C2BC),
        outline = Color(0xFFA08C87),
        outlineVariant = Color(0xFF53433F),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF1DFDA),
        inverseOnSurface = Color(0xFF392E2B),
        inversePrimary = Color(0xFF8F4C36),
        surfaceDim = Color(0xFF1A110F),
        surfaceBright = Color(0xFF423734),
        surfaceContainerLowest = Color(0xFF140C0A),
        surfaceContainerLow = Color(0xFF231917),
        surfaceContainer = Color(0xFF271D1B),
        surfaceContainerHigh = Color(0xFF322825),
        surfaceContainerHighest = Color(0xFF3D322F),
    )

    override val mediumContrastLightColorScheme = lightColorScheme(
        primary = Color(0xFF6D311E),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFAA614A),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF593C33),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF8F6D62),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF4E4216),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF827443),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFF8C0009),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFDA342E),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFFFF8F6),
        onBackground = Color(0xFF231917),
        surface = Color(0xFFFFF8F6),
        onSurface = Color(0xFF231917),
        surfaceVariant = Color(0xFFF5DED7),
        onSurfaceVariant = Color(0xFF4F3F3B),
        outline = Color(0xFF6C5B56),
        outlineVariant = Color(0xFF897771),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF392E2B),
        inverseOnSurface = Color(0xFFFFEDE8),
        inversePrimary = Color(0xFFFFB59E),
        surfaceDim = Color(0xFFE8D6D1),
        surfaceBright = Color(0xFFFFF8F6),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFFF1ED),
        surfaceContainer = Color(0xFFFCEAE5),
        surfaceContainerHigh = Color(0xFFF7E4DF),
        surfaceContainerHighest = Color(0xFFF1DFDA),
    )

    override val highContrastLightColorScheme = lightColorScheme(
        primary = Color(0xFF431203),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF6D311E),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF341C14),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF593C33),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF2A2100),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF4E4216),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFF4E0002),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF8C0009),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFFFF8F6),
        onBackground = Color(0xFF231917),
        surface = Color(0xFFFFF8F6),
        onSurface = Color(0xFF000000),
        surfaceVariant = Color(0xFFF5DED7),
        onSurfaceVariant = Color(0xFF2E211D),
        outline = Color(0xFF4F3F3B),
        outlineVariant = Color(0xFF4F3F3B),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF392E2B),
        inverseOnSurface = Color(0xFFFFFFFF),
        inversePrimary = Color(0xFFFFE7E0),
        surfaceDim = Color(0xFFE8D6D1),
        surfaceBright = Color(0xFFFFF8F6),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFFF1ED),
        surfaceContainer = Color(0xFFFCEAE5),
        surfaceContainerHigh = Color(0xFFF7E4DF),
        surfaceContainerHighest = Color(0xFFF1DFDA),
    )

    override val mediumContrastDarkColorScheme = darkColorScheme(
        primary = Color(0xFFFFBBA6),
        onPrimary = Color(0xFF310800),
        primaryContainer = Color(0xFFCB7C64),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFEBC1B5),
        onSecondary = Color(0xFF261009),
        secondaryContainer = Color(0xFFAE887D),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFDCCA91),
        onTertiary = Color(0xFF1C1600),
        tertiaryContainer = Color(0xFF9F905C),
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFFFBAB1),
        onError = Color(0xFF370001),
        errorContainer = Color(0xFFFF5449),
        onErrorContainer = Color(0xFF000000),
        background = Color(0xFF1A110F),
        onBackground = Color(0xFFF1DFDA),
        surface = Color(0xFF1A110F),
        onSurface = Color(0xFFFFF9F8),
        surfaceVariant = Color(0xFF53433F),
        onSurfaceVariant = Color(0xFFDCC6C0),
        outline = Color(0xFFB39E99),
        outlineVariant = Color(0xFF927F7A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF1DFDA),
        inverseOnSurface = Color(0xFF322825),
        inversePrimary = Color(0xFF733622),
        surfaceDim = Color(0xFF1A110F),
        surfaceBright = Color(0xFF423734),
        surfaceContainerLowest = Color(0xFF140C0A),
        surfaceContainerLow = Color(0xFF231917),
        surfaceContainer = Color(0xFF271D1B),
        surfaceContainerHigh = Color(0xFF322825),
        surfaceContainerHighest = Color(0xFF3D322F),
    )

    override val highContrastDarkColorScheme = darkColorScheme(
        primary = Color(0xFFFFF9F8),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFFFFBBA6),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFFFF9F8),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFFEBC1B5),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFFFFAF5),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFFDCCA91),
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFFFF9F9),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFFFFBAB1),
        onErrorContainer = Color(0xFF000000),
        background = Color(0xFF1A110F),
        onBackground = Color(0xFFF1DFDA),
        surface = Color(0xFF1A110F),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF53433F),
        onSurfaceVariant = Color(0xFFFFF9F8),
        outline = Color(0xFFDCC6C0),
        outlineVariant = Color(0xFFDCC6C0),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF1DFDA),
        inverseOnSurface = Color(0xFF000000),
        inversePrimary = Color(0xFF4D1908),
        surfaceDim = Color(0xFF1A110F),
        surfaceBright = Color(0xFF423734),
        surfaceContainerLowest = Color(0xFF140C0A),
        surfaceContainerLow = Color(0xFF231917),
        surfaceContainer = Color(0xFF271D1B),
        surfaceContainerHigh = Color(0xFF322825),
        surfaceContainerHighest = Color(0xFF3D322F),
    )
}

@Preview
@Composable
private fun PreviewThemeNormal() {
    PreviewTheme(RedTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeMedium() {
    PreviewTheme(RedTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeHome() {
    PreviewTheme(RedTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeDark() {
    PreviewTheme(RedTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeDarkMedium() {
    PreviewTheme(RedTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeDarkHome() {
    PreviewTheme(RedTheme, DARK_MODE, HIGH)
}