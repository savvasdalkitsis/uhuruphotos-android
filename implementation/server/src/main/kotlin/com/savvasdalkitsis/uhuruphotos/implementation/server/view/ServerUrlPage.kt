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
package com.savvasdalkitsis.uhuruphotos.implementation.server.view

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.AttemptChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.UrlTyped

@Composable
internal fun BoxScope.ServerUrlPage(
    state: ServerState.ServerUrl,
    action: (ServerAction) -> Unit
) {
    var serverTextFieldValue by remember { mutableStateOf(state.prefilledUrl) }
    Feedback(state, action)
    Column(modifier = Modifier.align(Alignment.Center)) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(string.enter_libre_photos_url)
        )
        OutlinedTextField(
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { action(AttemptChangeServerUrlTo(serverTextFieldValue)) }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "serverIcon"
                )
            },
            label = { Text(stringResource(string.server_url)) },
            value = serverTextFieldValue,
            isError = !state.isUrlValid,
            onValueChange = {
                serverTextFieldValue = it
                action(UrlTyped(it))
            },
        )
        Button(
            enabled = state.allowSaveUrl,
            onClick = { action(AttemptChangeServerUrlTo(serverTextFieldValue)) }
        ) {
            Text(stringResource(string.save))
        }
    }
    if (state.showUnsecureServerConfirmation) {
        UnsecuredServerConfirmationDialog(serverTextFieldValue, action)
    }
}