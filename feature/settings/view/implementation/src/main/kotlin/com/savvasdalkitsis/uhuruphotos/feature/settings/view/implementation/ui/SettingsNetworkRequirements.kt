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
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.change
import uhuruphotos_android.foundation.strings.api.generated.resources.network_requirements

@Composable
internal fun SettingsNetworkRequirements(
    networkType: NetworkType?,
    action: (SettingsAction) -> Unit,
    selection: (NetworkType) -> SettingsAction,
) {
    SettingsTextDropDownButtonRow(
        text = stringResource(
            string.network_requirements,
            networkType.friendlyName()
        ),
        buttonText = stringResource(string.change),
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