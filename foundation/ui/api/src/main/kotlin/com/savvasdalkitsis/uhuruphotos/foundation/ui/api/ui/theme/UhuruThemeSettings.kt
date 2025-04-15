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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ContentTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.Checkable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox.UhuruCheckBoxRow
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.slider.UhuruSliderRow
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.UhuruTextRow
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.state.ThemeSettingsState
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.collage_shape
import uhuruphotos_android.foundation.strings.api.generated.resources.collage_spacing
import uhuruphotos_android.foundation.strings.api.generated.resources.collage_spacing_include_edges
import uhuruphotos_android.foundation.strings.api.generated.resources.theme

@Composable
fun ColumnScope.UhuruThemeSettings(
    state: ThemeSettingsState = ThemeSettingsState(),
    onCollageSpacingChanged: (Int) -> Unit = {},
    onCollageSpacingEdgeChanged: (Boolean) -> Unit = {},
    onChangeThemeVariant: (ThemeVariant, ThemeContrast) -> Unit = { _, _ -> },
    onChangeThemeMode: (ThemeMode) -> Unit = {},
    onCollageShapeChanged: (CollageShape) -> Unit = {},
) {
    BottomAppBar {
        ThemeMode.entries.forEach { themeMode ->
            ThemeBottomItem(themeMode, state, onChangeThemeMode)
        }
    }
    Text(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        text = stringResource(string.theme),
        textAlign = TextAlign.Center
    )
    val themes = remember {
        ThemeVariant.validEntries()
    }
    HorizontalPager(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        state = rememberPagerState(initialPage = themes.indexOf(state.theme)) { themes.size },
        pageSpacing = (-52).dp,
        contentPadding = PaddingValues(horizontal = 64.dp),
    ) { page ->
        ThemeItem(themes[page], state, onChangeThemeVariant)
    }
    UhuruSliderRow(
        text = {
            stringResource(string.collage_spacing, it.toInt())
        },
        initialValue = state.collageSpacing.toFloat(),
        range = 0f..6f,
        steps = 7,
        onValueChanged = { onCollageSpacingChanged(it.toInt()) }
    )
    UhuruCheckBoxRow(
        text = stringResource(string.collage_spacing_include_edges),
        icon = drawable.ic_border_outside,
        isChecked = state.collageSpacingIncludeEdges,
        onCheckedChange = { onCollageSpacingEdgeChanged(it) },
    )
    UhuruTextRow(stringResource(string.collage_shape))

    BottomAppBar {
        CollageShape.entries.forEach { shape ->
            ShapeBottomItem(shape, state, onCollageShapeChanged)
        }
    }
}

@Composable
private fun ThemeItem(
    themeVariant: ThemeVariant,
    state: ThemeSettingsState,
    onThemeVariantChanged: (ThemeVariant, ThemeContrast) -> Unit = { _, _ -> },
) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(8.dp),
    ) {
        themeVariant.supportedContrasts.forEach { contrast ->
            ThemeItem(Modifier.fillMaxWidth(), themeVariant, contrast, state, onThemeVariantChanged)
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ThemeItem(
    modifier: Modifier,
    themeVariant: ThemeVariant,
    themeContrast: ThemeContrast,
    state: ThemeSettingsState,
    onThemeVariantChanged: (ThemeVariant, ThemeContrast) -> Unit = { _, _ -> },
) {
    ContentTheme(
        themeMode = state.themeMode,
        theme = themeVariant,
        themeContrast = themeContrast,
    ) {
        Checkable(
            id = themeVariant.label,
            shape = CardDefaults.outlinedShape,
            selectedBorder = CardDefaults.outlinedCardBorder(),
            selectionMode = if (state.theme == themeVariant && state.themeContrast == themeContrast)
                SELECTED
            else
                UNDEFINED,
            onClick = {
                onThemeVariantChanged(themeVariant, themeContrast)
            }
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = 50.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = spacedBy(8.dp),
            ) {
                Text("${stringResource(themeVariant.label)} - ${stringResource(themeContrast.label)}")

                ThemeSwatch(MaterialTheme.colorScheme.primaryContainer)
                ThemeSwatch(MaterialTheme.colorScheme.secondaryContainer)
                ThemeSwatch(MaterialTheme.colorScheme.errorContainer)
            }
        }
    }
}

@Composable
private fun ThemeSwatch(color: Color) {
    Box(
        Modifier
            .clip(CardDefaults.shape)
            .background(color)
            .height(24.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun RowScope.ThemeBottomItem(
    expectedState: ThemeMode,
    state: ThemeSettingsState,
    onChangeThemeMode: (ThemeMode) -> Unit = {},
) {
    NavigationBarItem(
        label = { Text(expectedState.friendlyName) },
        icon = {
            Icon(painter = painterResource(expectedState.icon), contentDescription = null)
        },
        selected = state.themeMode == expectedState,
        onClick = { onChangeThemeMode(expectedState) }
    )
}

@Composable
private fun RowScope.ShapeBottomItem(
    expectedState: CollageShape,
    state: ThemeSettingsState,
    onCollageShapeChanged: (CollageShape) -> Unit = {},
) {
    NavigationBarItem(
        label = { Text(stringResource(expectedState.text)) },
        icon = {
            Icon(painter = painterResource(expectedState.icon), contentDescription = null)
        },
        selected = state.collageShape == expectedState,
        onClick = { onCollageShapeChanged(expectedState) }
    )
}

@Preview
@Composable
private fun SettingsThemePreview() {
    PreviewAppTheme {
        Column {
            UhuruThemeSettings()
        }
    }
}

@Preview
@Composable
private fun SettingsThemePreviewDark() {
    PreviewAppTheme(themeMode = DARK_MODE) {
        Column {
            UhuruThemeSettings()
        }
    }
}