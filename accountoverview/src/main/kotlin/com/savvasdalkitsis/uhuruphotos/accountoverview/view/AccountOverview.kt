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
package com.savvasdalkitsis.uhuruphotos.accountoverview.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.UserBadge
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState
import com.savvasdalkitsis.uhuruphotos.strings.R as Strings

@Composable
internal fun AccountOverview(
    userInformationState: UserInformationState,
    onLogoutClicked: () -> Unit,
    onEditServerClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserBadge(
                state = userInformationState,
                size = 48.dp
            )
            Column {
                Text(
                    text = userInformationState.userFullName,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = userInformationState.serverUrl,
                    style = MaterialTheme.typography.caption.copy(color = Color.Gray),
                )
            }
            ActionIcon(
                onClick = onEditServerClicked,
                icon = R.drawable.ic_edit,
                contentDescription = stringResource(Strings.string.edit_server_url)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onSettingsClicked
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(Strings.string.settings))
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onLogoutClicked,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(Strings.string.log_out))
            }
        }
    }
}