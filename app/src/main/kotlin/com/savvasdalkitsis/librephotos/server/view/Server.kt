package com.savvasdalkitsis.librephotos.server.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.main.MainScaffolding
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.*
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState.*

@Composable
fun Server(
    state: ServerState,
    action: (ServerAction) -> Unit = {},
) {
    MainScaffolding { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            when (state) {
                Loading ->
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                is ServerUrl -> Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "Enter LibrePhotos server url:"
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { action(ChangeServerUrlTo(state.url)) }
                        ),
                        leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = "serverIcon") },
                        label = { Text("Server Url") },
                        value = state.url,
                        onValueChange = {
                            action(UrlTyped(it))
                        },
                    )
                    Button(onClick = { action(ChangeServerUrlTo(state.url)) }) {
                        Text("Save")
                    }
                }
                is UserCredentials -> {
                    Column {
                        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                        Row {
                            IconButton(
                                onClick = { action(RequestServerUrlChange) },
                                modifier = Modifier.align(CenterVertically)
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Change server url"
                                )
                            }
                            Text(
                                text = "Change server url",
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                    }
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = "Login to server"
                        )
                        OutlinedTextField(
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "emailIcon") },
                            label = { Text("User email address") },
                            value = state.userEmail,
                            onValueChange = {
                                action(UserEmailChangedTo(it))
                            },
                        )
                        OutlinedTextField(
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { action(Login) }
                            ),
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "passwordIcon") },
                            label = { Text("User password") },
                            value = state.password,
                            onValueChange = {
                                action(UserPasswordChangedTo(it))
                            },
                        )
                        Button(onClick = { action(Login) }) {
                            Text("Login")
                        }
                    }
                }
            }
        }
    }
}