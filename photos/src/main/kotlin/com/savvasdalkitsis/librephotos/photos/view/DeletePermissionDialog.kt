package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun DeletePermissionDialog(
    photoCount: Int,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            when (photoCount) {
                1 -> Text("Delete photo")
                else -> Text("Delete $photoCount photos")
            }
        },
        text = {
            when (photoCount) {
                1 -> Text("Are you sure you want to delete this photo?")
                else -> Text("Are you sure you want to delete these $photoCount photos?")
            }
        },
        confirmButton = {
            Button(onClick = onDelete) {
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