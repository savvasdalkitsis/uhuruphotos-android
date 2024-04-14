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
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.ReverseDirection

@Composable
fun MultiButtonDialog(
    title: String,
    onDismiss: () -> Unit,
    confirmButton: @Composable FlowRowScope.() -> Unit,
    extraButtons: @Composable RowScope.()-> Unit = {},
    negativeButtonText: String = stringResource(string.no),
    dismissButton: @Composable FlowRowScope.() -> Unit = {
        OutlinedButton(
            modifier = Modifier
                .recomposeHighlighter()
                .weight(1f),
            onClick = onDismiss,
        ) {
            Text(negativeButtonText)
        }
    },
    body: @Composable ColumnScope.() -> Unit,
) {
    PaddedDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, style = MaterialTheme.typography.h5)
        },
        text = {
            Column(
                verticalArrangement = spacedBy(8.dp)
            ) {
                body()
            }
        },
        buttons = {
            ReverseDirection {
                extraButtons()
            }
            ReverseDirection {
                dismissButton()
            }

            ReverseDirection {
                confirmButton()
            }
        },
    )
}