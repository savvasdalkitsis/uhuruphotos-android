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

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeCloudSyncChargingRequirements
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeCloudSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox.UhuruCheckBoxRow
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_power
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.requires_charging

@Composable
internal fun SettingsJobsCloudSyncConfiguration(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    UhuruCheckBoxRow(
        text = stringResource(string.requires_charging),
        icon = drawable.ic_power,
        isChecked = state.cloudSyncRequiresCharging,
    ) { selected ->
        action(ChangeCloudSyncChargingRequirements(selected))
    }
    HorizontalDivider()
    SettingsNetworkRequirements(state.cloudSyncNetworkRequirement, action) {
        ChangeCloudSyncNetworkRequirements(it)
    }
}