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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.Login
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.SendLogsClick

@Composable
internal fun UserCredentialsPage(
    action: (ServerAction) -> Unit,
    state: ServerState.UserCredentials
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            onClick = { action(SendLogsClick) }
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_feedback), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Send logs for troubleshooting")
        }
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
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Login to server"
            )
            UsernameField(state, action)
            PasswordField(state, action)
            Button(
                enabled = state.allowLogin,
                onClick = { action(Login) }
            ) {
                Text("Login")
            }
        }
    }
}