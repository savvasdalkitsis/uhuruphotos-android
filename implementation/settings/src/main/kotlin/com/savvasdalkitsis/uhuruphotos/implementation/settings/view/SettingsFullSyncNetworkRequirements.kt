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
import androidx.work.NetworkType
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.METERED
import androidx.work.NetworkType.NOT_ROAMING
import androidx.work.NetworkType.UNMETERED
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ChangeFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.api.strings.R

@Composable
private fun NetworkType?.friendlyName(): String =  when (this) {
        CONNECTED -> stringResource(R.string.any_connected)
        UNMETERED -> stringResource(R.string.unmetered)
        NOT_ROAMING -> stringResource(R.string.not_roaming)
        METERED -> stringResource(R.string.metered)
        else -> "-"
    }

@Composable
internal fun SettingsFullSyncNetworkRequirements(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsTextDropDownButtonRow(
        text = stringResource(R.string.network_requirements,
            state.fullSyncNetworkRequirement.friendlyName()
        ),
        buttonText = stringResource(R.string.change),
        action = action,
    ) {
        @Composable
        fun item(networkType: NetworkType) {
            Item(networkType.friendlyName(), ChangeFullSyncNetworkRequirements(networkType))
        }
        item(CONNECTED)
        item(UNMETERED)
        item(METERED)
        item(NOT_ROAMING)
    }
}