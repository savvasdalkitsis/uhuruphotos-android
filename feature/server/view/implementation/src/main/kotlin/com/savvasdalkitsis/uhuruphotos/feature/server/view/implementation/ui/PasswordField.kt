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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.TogglePasswordVisibility
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.UserPasswordChangedTo
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon

@Composable
internal fun PasswordField(
    state: ServerState,
    action: (ServerAction) -> Unit
) {
    var password by remember(state.password) {
        mutableStateOf(state.password)
    }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        singleLine = true,
        visualTransformation = when {
            state.passwordVisible -> VisualTransformation.None
            else -> PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                action(Login(
                    allowUnsecuredServers = false,
                    rememberCredentials = state.rememberCredentials,
                ))
            }
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "passwordIcon"
            )
        },
        trailingIcon = {
            UhuruActionIcon(
                onClick = { action(TogglePasswordVisibility) },
                icon = when {
                    state.passwordVisible -> drawable.ic_visible
                    else -> drawable.ic_invisible
                }
            )
        },
        label = { Text(stringResource(string.password)) },
        value = password,
        onValueChange = {
            password = it
            action(UserPasswordChangedTo(it))
        },
    )
}