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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.MultiButtonDialog

@Composable
internal fun DeleteFullySyncedPermissionDialog(
    onDismiss: () -> Unit,
    onDeleteLocalTrashRemote: () -> Unit,
    onDeleteLocal: () -> Unit,
) {
    MultiButtonDialog(
        title = stringResource(string.media_sync_status_fully_synced),
        onDismiss = onDismiss,
        confirmButton = {
            Button(onClick = onDeleteLocalTrashRemote) {
                Text(stringResource(string.delete_fully_synced_media_both))
            }
        },
        extraButtons = listOf {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onDeleteLocal,
            ) {
                Text(stringResource(string.delete_fully_synced_media_local))
            }
        },
        negativeButtonText = stringResource(string.cancel),
        body = {
            Text(
                stringResource(string.what_would_like_to_do)
            )
            Text(stringResource(string.operation_irreverisble))
        },
    )
}