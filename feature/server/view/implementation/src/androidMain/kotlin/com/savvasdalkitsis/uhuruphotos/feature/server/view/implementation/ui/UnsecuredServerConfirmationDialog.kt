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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.DismissUnsecuredServerDialog
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun UnsecuredServerConfirmationDialog(
    currentUrl: String,
    action: (ServerAction) -> Unit,
    state: ServerState
) {
    YesNoDialog(
        title = stringResource(strings.unsecured_server),
        onDismiss = { action(DismissUnsecuredServerDialog) },
        onYes = {
            action(DismissUnsecuredServerDialog)
            action(Login(
                allowUnsecuredServers = true,
                rememberCredentials = state.rememberCredentials,
            ))
        },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(strings.unsecured_server_are_you_sure))
            Text(stringResource(strings.unsecured_server_not_encrypted, currentUrl))
        }
    }
}