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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R

@Composable
internal fun SettingsNetworkRequirements(
    networkType: NetworkType?,
    action: (SettingsAction) -> Unit,
    selection: (NetworkType) -> SettingsAction,
) {
    SettingsTextDropDownButtonRow(
        text = stringResource(
            R.string.network_requirements,
            networkType.friendlyName()
        ),
        buttonText = stringResource(R.string.change),
        action = action,
    ) {
        @Composable
        fun item(networkType: NetworkType) {
            Item(networkType.friendlyName(), selection(networkType))
        }
        item(NetworkType.CONNECTED)
        item(NetworkType.UNMETERED)
        item(NetworkType.METERED)
        item(NetworkType.NOT_ROAMING)
    }
}