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
package com.savvasdalkitsis.uhuruphotos.server.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.*

@Composable
fun BoxScope.UserCredentialsPage(
    contentPadding: PaddingValues,
    action: (ServerAction) -> Unit,
    state: ServerState.UserCredentials
) {
    OutlinedButton(
        modifier = Modifier.align(Alignment.TopEnd),
        onClick = { action(SendLogsClick) }
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_feedback), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Send logs for troubleshooting")
    }
    Column {
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
        Row(
            modifier = Modifier.clickable { action(RequestServerUrlChange) }
        ) {
            IconButton(
                onClick = { action(RequestServerUrlChange) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Change server url"
                )
            }
            Text(
                text = "Change server url",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
    Column(modifier = Modifier.align(Alignment.Center)) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Login to server"
        )
        OutlinedTextField(
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "usernameIcon"
                )
            },
            label = { Text("Username") },
            value = state.username,
            onValueChange = {
                action(UsernameChangedTo(it))
            },
        )
        OutlinedTextField(
            maxLines = 1,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
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
            label = { Text("User password") },
            value = state.password,
            onValueChange = {
                action(UserPasswordChangedTo(it))
            },
        )
        Button(
            enabled = state.allowLogin,
            onClick = { action(Login) }
        ) {
            Text("Login")
        }
    }
}