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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction

@Composable
internal fun SettingsFullFeedSyncPermissionDialog(
    action: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(string.perform_full_feed_sync)) },
        text = {
            Column {
                Text(stringResource(string.are_you_sure_you_want_to_perform_full_sync))
                Text(
                    stringResource(string.process_takes_while_consumes_battery),
                    style = MaterialTheme.typography.caption
                )
            }
        },
        confirmButton = {
            Button(onClick = { action(SettingsAction.PerformFullFeedSync) }) {
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