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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.HIGH
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.MEDIUM
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.NORMAL
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.LIGHT_MODE

@Preview
@Composable
private fun PreviewThemeBlueNormal() {
    PreviewTheme(BlueTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeBlueMedium() {
    PreviewTheme(BlueTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeBlueHome() {
    PreviewTheme(BlueTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeBlueDark() {
    PreviewTheme(BlueTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeBlueDarkMedium() {
    PreviewTheme(BlueTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeBlueDarkHome() {
    PreviewTheme(BlueTheme, DARK_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeGreenNormal() {
    PreviewTheme(GreenTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeGreenMedium() {
    PreviewTheme(GreenTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeGreenHome() {
    PreviewTheme(GreenTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeGreenDark() {
    PreviewTheme(GreenTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeGreenDarkMedium() {
    PreviewTheme(GreenTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeGreenDarkHome() {
    PreviewTheme(GreenTheme, DARK_MODE, HIGH)
}


@Preview
@Composable
private fun PreviewThemeRedNormal() {
    PreviewTheme(RedTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeRedMedium() {
    PreviewTheme(RedTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeRedHome() {
    PreviewTheme(RedTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeRedDark() {
    PreviewTheme(RedTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeRedDarkMedium() {
    PreviewTheme(RedTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeRedDarkHome() {
    PreviewTheme(RedTheme, DARK_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeYellowNormal() {
    PreviewTheme(YellowTheme, LIGHT_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeYellowMedium() {
    PreviewTheme(YellowTheme, LIGHT_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeYellowHome() {
    PreviewTheme(YellowTheme, LIGHT_MODE, HIGH)
}

@Preview
@Composable
private fun PreviewThemeYellowDark() {
    PreviewTheme(YellowTheme, DARK_MODE, NORMAL)
}

@Preview
@Composable
private fun PreviewThemeYellowDarkMedium() {
    PreviewTheme(YellowTheme, DARK_MODE, MEDIUM)
}

@Preview
@Composable
private fun PreviewThemeYellowDarkHome() {
    PreviewTheme(YellowTheme, DARK_MODE, HIGH)
}