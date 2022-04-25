package com.savvasdalkitsis.uhuruphotos.account.view

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun LogOutConfirmationDialog(
    onDismiss: () -> Unit,
    onLogOut: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log out") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(onClick = onLogOut) {
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