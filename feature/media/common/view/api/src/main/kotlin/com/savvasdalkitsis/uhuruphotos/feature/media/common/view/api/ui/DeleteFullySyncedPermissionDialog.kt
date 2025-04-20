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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.UhuruIconOutlineButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.CancelDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_delete
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_folder_network
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_fully_synced_media_both
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_fully_synced_media_local
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_fully_synced
import uhuruphotos_android.foundation.strings.api.generated.resources.operation_irreverisble
import uhuruphotos_android.foundation.strings.api.generated.resources.what_would_like_to_do

@Composable
fun DeleteFullySyncedPermissionDialog(
    onDismiss: () -> Unit,
    onDeleteLocalTrashRemote: () -> Unit,
    onDeleteLocal: () -> Unit,
) {
    CancelDialog(
        title = stringResource(string.media_sync_status_fully_synced),
        onDismiss = onDismiss,
        body = {
            Text(stringResource(string.what_would_like_to_do))
            UhuruIconOutlineButton(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = drawable.ic_delete,
                text = stringResource(string.delete_fully_synced_media_local),
                onClick = onDeleteLocal,
            )
            UhuruIconOutlineButton(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = drawable.ic_folder_network,
                text = stringResource(string.delete_fully_synced_media_both),
                onClick = onDeleteLocalTrashRemote,
            )
            AlertText(text = stringResource(string.operation_irreverisble))
        },
    )
}

@Preview
@Composable
private fun DeleteFullySyncedPermissionDialogPreview() {
    PreviewAppTheme {
        DeleteFullySyncedPermissionDialog(
            onDismiss = {},
            onDeleteLocalTrashRemote = {},
            onDeleteLocal = {},
        )
    }
}