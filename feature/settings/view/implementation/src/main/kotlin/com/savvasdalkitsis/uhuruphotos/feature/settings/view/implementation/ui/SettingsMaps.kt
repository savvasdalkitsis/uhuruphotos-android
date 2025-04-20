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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeMapProvider
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.MapProviderState
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import kotlinx.collections.immutable.toImmutableSet
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_map

@Composable
internal fun ColumnScope.SettingsMaps(
    mapProviderState: MapProviderState.SelectedState,
    action: (SettingsAction) -> Unit,
) {
    BottomAppBar {
        mapProviderState.available.forEach { provider ->
            BottomItem(provider, mapProviderState, action)
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    expectedState: MapProvider,
    state: MapProviderState.SelectedState,
    action: (SettingsAction) -> Unit
) {
    NavigationBarItem(
        label = { Text(expectedState.name) },
        icon = {
            Icon(painter = painterResource(drawable.ic_map), contentDescription = null)
        },
        selected = state.current == expectedState,
        onClick = { action(ChangeMapProvider(expectedState)) }
    )
}

@Preview
@Composable
fun SettingsMapPreview() {
    PreviewAppTheme {
        Column {
            SettingsMaps(
                mapProviderState = MapProviderState.SelectedState(
                    current = MapProvider.Google,
                    available = MapProvider.entries.toImmutableSet()
                ),
            ){}
        }
    }
}

@Preview
@Composable
fun SettingsMapPreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        Column {
            SettingsMaps(
                mapProviderState = MapProviderState.SelectedState(
                    current = MapProvider.Google,
                    available = MapProvider.entries.toImmutableSet()
                ),
            ){}
        }
    }
}