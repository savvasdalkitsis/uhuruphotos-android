package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.DeletePhoto

@Composable
fun DeletePermissionDialog(action: (PhotoAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { action(PhotoAction.DismissPhotoDeletionDialog) },
        title = { Text("Delete photo") },
        text = { Text("Are you sure you want to delete this photo?") },
        confirmButton = {
            Button(onClick = { action(DeletePhoto) }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = { action(PhotoAction.DismissPhotoDeletionDialog) }) {
                Text("No")
            }
        },
    )
}