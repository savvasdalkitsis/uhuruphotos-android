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

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox.UhuruCheckBoxRow
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_gps_off
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_gps_on
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.remove_gps_data_when_sharing

@Composable
internal fun SettingsShare(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    val checked = state.shareRemoveGpsDataEnabled
    UhuruCheckBoxRow(
        text = stringResource(string.remove_gps_data_when_sharing),
        icon = when {
            checked -> drawable.ic_gps_off
            else -> drawable.ic_gps_on
        },
        isChecked = checked,
        onCheckedChange = { action(ChangeShareGpsDataEnabled(it)) }
    )
}