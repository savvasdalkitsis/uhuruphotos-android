package com.savvasdalkitsis.librephotos.server.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.main.MainScaffolding
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState

@Composable
fun Server(
    state: ServerState,
    action: (ServerAction) -> Unit = {},
) {
    MainScaffolding {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            } else if (state.serverUrl != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "Enter LibrePhotos server url:"
                    )
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { action(ServerAction.ServerUrlChange) }
                        ),
                        leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = "serverIcon") },
                        label = { Text("Server Url") },
                        value = state.serverUrl,
                        onValueChange = {
                            action(ServerAction.UrlChangedTo(it))
                        },
                    )
                }
            }
        }
    }
}