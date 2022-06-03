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
package com.savvasdalkitsis.uhuruphotos.server.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.server.seam.ServerAction.DismissUnsecuredServerDialog

@Composable
internal fun UnsecuredServerConfirmationDialog(
    currentUrl: String,
    action: (ServerAction) -> Unit
) {
    AlertDialog(
        onDismissRequest = { action(DismissUnsecuredServerDialog) },
        title = { Text("Unsecured server") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Are you sure you want to connect to an unsecured server?")
                Text("This server communicates over plain http and is not encrypted: $currentUrl")
            }
        },
        confirmButton = {
            Button(onClick = { action(ChangeServerUrlTo(currentUrl)) }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = { action(DismissUnsecuredServerDialog) }) {
                Text("No")
            }
        },
    )
}