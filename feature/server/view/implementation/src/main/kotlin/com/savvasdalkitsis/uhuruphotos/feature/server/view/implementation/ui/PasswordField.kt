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
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.TogglePasswordVisibility
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.UserPasswordChangedTo
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
internal fun PasswordField(
    state: ServerState.UserCredentials,
    action: (ServerAction) -> Unit
) {
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
            onDone = { action(Login) }
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "passwordIcon"
            )
        },
        trailingIcon = {
            ActionIcon(
                onClick = { action(TogglePasswordVisibility) },
                icon = when {
                    state.passwordVisible -> drawable.ic_visible
                    else -> drawable.ic_invisible
                }
            )
        },
        label = { Text("User password") },
        value = state.password,
        onValueChange = {
            action(UserPasswordChangedTo(it))
        },
    )
}