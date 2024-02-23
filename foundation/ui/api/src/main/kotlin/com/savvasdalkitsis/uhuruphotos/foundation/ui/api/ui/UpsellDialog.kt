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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme

@Composable
fun UpsellDialog(
    onDismiss: () -> Unit,
    onNeverAgain: (() -> Unit)? = null,
    onLogin: () -> Unit,
) {
    MultiButtonDialog(
        title = stringResource(R.string.advanced_feature_title),
        dismissButton = {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .recomposeHighlighter(),
                onClick = onDismiss,
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        onDismiss = onDismiss,
        extraButtons = if (onNeverAgain != null) listOf {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .recomposeHighlighter(),
                onClick = onNeverAgain
            ) {
                Text(stringResource(R.string.do_not_show_again))
            }
        } else emptyList(),
        confirmButton = { LoginButton(onLogin = onLogin) },
    ) {
        Text(stringResource(R.string.advanced_feature_body))
    }
}

@Preview
@Composable
fun UpsellDialogPreview() {
    PreviewAppTheme {
        UpsellDialog(
            onDismiss = {},
            onNeverAgain = {},
            onLogin = {}
        )
    }
}
