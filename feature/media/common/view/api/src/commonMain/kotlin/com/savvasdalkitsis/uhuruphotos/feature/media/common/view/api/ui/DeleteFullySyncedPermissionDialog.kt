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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.IconOutlineButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.CancelDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DeleteFullySyncedPermissionDialog(
    onDismiss: () -> Unit,
    onDeleteLocalTrashRemote: () -> Unit,
    onDeleteLocal: () -> Unit,
) {
    CancelDialog(
        title = stringResource(strings.media_sync_status_fully_synced),
        onDismiss = onDismiss,
        body = {
            Text(stringResource(strings.what_would_like_to_do))
            IconOutlineButton(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = images.ic_delete,
                text = stringResource(strings.delete_fully_synced_media_local),
                onClick = onDeleteLocal,
            )
            IconOutlineButton(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = images.ic_folder_network,
                text = stringResource(strings.delete_fully_synced_media_both),
                onClick = onDeleteLocalTrashRemote,
            )
            AlertText(text = stringResource(strings.operation_irreverisble))
        },
    )
}