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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme

@Composable
fun YesNoDialog(
    title: String,
    onDismiss: () -> Unit,
    onYes: () -> Unit,
    body: @Composable ColumnScope.() -> Unit,
) {
    YesNoDialog(title, onDismiss, onYes, stringResource(string.yes), stringResource(string.no), body)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YesNoDialog(
    title: String,
    onNo: () -> Unit,
    onYes: () -> Unit,
    yes: String,
    no: String,
    body: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = onNo,
        confirmButton = {
            Button(onClick = onYes) {
                Text(yes)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onNo,
            ) {
                Text(no)
            }
        },
        title = {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column(
                verticalArrangement = spacedBy(8.dp)
            ) {
                body()
            }
        },
    )
}

@Preview
@Composable
private fun WelcomeNeedsAccessDialogPreview() {
    PreviewAppTheme {
        YesNoDialog(
            title = "Title",
            onDismiss = {},
            onYes = {},
        ) {
            Text("Body")
        }
    }
}