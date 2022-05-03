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

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.work.NetworkType
import androidx.work.NetworkType.*
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeFullSyncNetworkRequirements

private val NetworkType?.friendlyName: String
    get() = when (this) {
        CONNECTED -> "Any connected"
        UNMETERED -> "Unmetered"
        NOT_ROAMING -> "Not roaming"
        METERED -> "Metered"
        else -> "-"
    }

@Composable
fun SettingsFullSyncNetworkRequirements(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsTextDropDownButtonRow(
        text = "Network requirements: ${state.fullSyncNetworkRequirement.friendlyName}",
        buttonText = "Change",
        action = action,
    ) {
        @Composable
        fun item(networkType: NetworkType) {
            Item(networkType.friendlyName, ChangeFullSyncNetworkRequirements(networkType))
        }
        item(CONNECTED)
        item(UNMETERED)
        item(METERED)
        item(NOT_ROAMING)
    }
}