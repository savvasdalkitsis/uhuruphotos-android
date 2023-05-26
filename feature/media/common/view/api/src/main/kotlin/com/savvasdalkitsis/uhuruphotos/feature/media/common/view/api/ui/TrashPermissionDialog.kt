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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
fun TrashPermissionDialog(
    mediaItemCount: Int,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        title = {
            Text(
                pluralStringResource(
                    R.plurals.trash_media,
                    mediaItemCount,
                    mediaItemCount
                )
            )
        },
        text = {
            Text(
                pluralStringResource(
                    R.plurals.trash_media_confirmation,
                    count = mediaItemCount,
                    mediaItemCount
                )
            )
        },
        confirmButton = {
            Button(onClick = onDelete) {
                Text(stringResource(string.yes))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(string.no))
            }
        },
    )
}