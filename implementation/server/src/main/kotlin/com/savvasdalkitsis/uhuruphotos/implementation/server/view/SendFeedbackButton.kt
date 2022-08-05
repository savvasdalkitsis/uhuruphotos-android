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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction

@Composable
internal fun ColumnScope.SendFeedbackButton(action: (ServerAction) -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .align(Alignment.End),
        onClick = { action(ServerAction.SendLogsClick) }
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_feedback), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(com.savvasdalkitsis.uhuruphotos.api.strings.R.string.send_feedback_with_logs))
    }
}