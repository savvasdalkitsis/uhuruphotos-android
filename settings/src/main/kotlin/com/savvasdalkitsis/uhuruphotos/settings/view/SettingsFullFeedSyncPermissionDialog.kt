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