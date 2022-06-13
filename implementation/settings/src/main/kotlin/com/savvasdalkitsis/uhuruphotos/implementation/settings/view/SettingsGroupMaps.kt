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

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ChangeMapProvider
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.MapProviderState

@Composable
internal fun SettingsGroupMaps(
    mapProviderState: MapProviderState.Selected,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.maps)) {
        SettingsTextDropDownButtonRow(
            text = "${stringResource(R.string.map_provider)}: ${mapProviderState.current.name}",
            buttonText = stringResource(R.string.change),
            action = action,
        ) {
            mapProviderState.available.forEach { provider ->
                Item(text = provider.name, action = ChangeMapProvider(provider))
            }
        }
    }
}