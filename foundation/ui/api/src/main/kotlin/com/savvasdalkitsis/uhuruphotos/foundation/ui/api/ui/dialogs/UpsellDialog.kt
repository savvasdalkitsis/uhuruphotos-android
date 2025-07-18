/*
Copyright 2023 Savvas Dalkitsis

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.LoginButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.WhatIsLibrePhotosButton
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.advanced_feature_body
import uhuruphotos_android.foundation.strings.api.generated.resources.advanced_feature_title
import uhuruphotos_android.foundation.strings.api.generated.resources.do_not_show_again
import uhuruphotos_android.foundation.strings.api.generated.resources.ok

@Composable
fun UpsellDialog(
    onDismiss: () -> Unit,
    onNeverAgain: (() -> Unit)? = null,
    onLogin: () -> Unit,
) {
    var showHelpDialog by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            WhatIsLibrePhotosButton {
                showHelpDialog = true
            }
            if (onNeverAgain != null) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .recomposeHighlighter(),
                    onClick = onNeverAgain
                ) {
                    Text(stringResource(string.do_not_show_again))
                }
            }
            LoginButton(onLogin = onLogin)
        },
        dismissButton = {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .recomposeHighlighter(),
                onClick = onDismiss,
            ) {
                Text(stringResource(string.ok))
            }
        },
        title = {
            Text(
                text = stringResource(string.advanced_feature_title),
                style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column(
                verticalArrangement = spacedBy(8.dp)
            ) {
                Text(stringResource(string.advanced_feature_body))
            }
        },
    )
    if (showHelpDialog) {
        WhatIsLibrePhotosDialog {
            showHelpDialog = false
            onDismiss()
        }
    }
}

@Preview
@Composable
private fun UpsellDialogPreview() {
    PreviewAppTheme {
        UpsellDialog(
            onDismiss = {},
            onNeverAgain = {}
        ) {}
    }
}
