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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.DismissHelpDialog
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.SendLogsClick
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ShowHelp
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.TakeUserToLibrePhotosWebsite
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.UpPressed
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.UrlTyped
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.MultiButtonDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.rememberCollapsibleGroupState

@Composable
internal fun Server(
    state: ServerState,
    action: (ServerAction) -> Unit = {},
) {
    CommonScaffold(
        modifier = Modifier
            .imeNestedScroll(),
        title = { Text(stringResource(string.libre_photos_server))},
        navigationIcon = { UpNavButton {
            action(UpPressed)
        }},
        actionBarContent = {
            ActionIcon(
                onClick = { action(ShowHelp) },
                icon = drawable.ic_help,
                contentDescription = stringResource(string.help)
            )
        }
    ) { contentPadding ->
        if (state.isLoading) {
            FullLoading()
        } else {
            var serverTextFieldValue by remember(state.prefilledUrl) {
                mutableStateOf(state.prefilledUrl)
            }
            Column(
                modifier = Modifier
                    .padding(contentPadding + PaddingValues(horizontal = 8.dp))
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CollapsibleGroup(
                    groupState = rememberCollapsibleGroupState(
                        title = string.server_url,
                        uniqueKey = "serverUrl",
                        initiallyCollapsed = false,
                    )
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Next,
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
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = { action(ShowHelp) }
                    ) {
                        Icon(
                            painter = painterResource(id = drawable.ic_help),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(string.what_is_libre_photos))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = stringResource(string.login_to_server)
                    )
                    UsernameField(state, action)
                    PasswordField(state, action)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.allowLogin,
                        onClick = { action(Login(allowUnsecuredServers = false)) }
                    ) {
                        Text(stringResource(string.login))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    EnableLoggingCheckbox(state, action)
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { action(SendLogsClick) }
                    ) {
                        Icon(
                            painter = painterResource(id = drawable.ic_feedback),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(string.send_feedback_with_logs))
                    }
                }
            }
            if (state.showUnsecureServerConfirmation) {
                UnsecuredServerConfirmationDialog(serverTextFieldValue, action)
            }
            if (state.showHelpDialog) {
                MultiButtonDialog(
                    title = stringResource(string.help),
                    onDismiss = { action(DismissHelpDialog) },
                    negativeButtonText = stringResource(string.ok),
                    confirmButton = {
                        Button(onClick = { action(TakeUserToLibrePhotosWebsite) }) {
                            Text(stringResource(string.take_me_to_website))
                        }
                    }
                ) {
                    Text(stringResource(string.libre_photos_description))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ServerPreview() {
    PreviewAppTheme {
        Server(state = ServerState(
            password = "pass",
            isLoggingEnabled = true,
            prefilledUrl = "http://librephotos.server",
            isUrlValid = true,
            showUnsecureServerConfirmation = false,
            username = "username",
            allowLogin = true,
            passwordVisible = false,
        ))
    }
}