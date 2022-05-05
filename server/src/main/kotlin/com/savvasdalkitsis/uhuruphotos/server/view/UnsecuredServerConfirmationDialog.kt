package com.savvasdalkitsis.uhuruphotos.server.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.DismissUnsecuredServerDialog

@Composable
fun UnsecuredServerConfirmationDialog(
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