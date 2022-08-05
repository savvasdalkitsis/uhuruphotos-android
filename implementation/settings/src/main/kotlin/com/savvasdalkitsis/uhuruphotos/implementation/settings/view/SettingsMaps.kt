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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ChangeMapProvider
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.MapProviderState
import com.savvasdalkitsis.uhuruphotos.api.icons.R as Icons

@Composable
internal fun ColumnScope.SettingsMaps(
    mapProviderState: MapProviderState.Selected,
    action: (SettingsAction) -> Unit,
) {
    SettingsTextDropDownButtonRow(
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painter = painterResource(Icons.drawable.ic_map), contentDescription = null)
                Text("${stringResource(string.map_provider)}: ${mapProviderState.current.name}",)
            }
        },
        buttonText = stringResource(string.change),
        action = action,
    ) {
        mapProviderState.available.forEach { provider ->
            Item(text = provider.name, action = ChangeMapProvider(provider))
        }
    }
}