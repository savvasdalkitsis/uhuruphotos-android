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
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.icons.R as Icons

@Composable
fun SettingsGroupShare(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsGroup(title = stringResource(R.string.share)) {
        val checked = state.shareRemoveGpsDataEnabled
        SettingsCheckBox(
            text = stringResource(R.string.remove_gps_data_when_sharing),
            icon = when {
                checked -> Icons.drawable.ic_gps_off
                else -> Icons.drawable.ic_gps_on
            },
            isChecked = checked,
            onCheckedChange = { action(ChangeShareGpsDataEnabled(it)) }
        )
    }
}