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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction

@Composable
fun SettingsFullFeedSyncPermissionDialog(
    action: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Perform full feed sync") },
        text = {
            Column {
                Text("Are you sure you want to start a full feed sync?")
                Text(
                    "This process takes a while and consumes battery.",
                    style = MaterialTheme.typography.caption
                )
            }
        },
        confirmButton = {
            Button(onClick = { action(SettingsAction.PerformFullFeedSync) }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        },
    )
}