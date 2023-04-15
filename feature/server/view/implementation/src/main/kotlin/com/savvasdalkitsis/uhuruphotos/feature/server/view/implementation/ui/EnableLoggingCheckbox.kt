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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.SetLoggingEnabled
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun ColumnScope.EnableLoggingCheckbox(
    state: ServerState,
    action: (ServerAction) -> Unit
) {
    Row(
        modifier = Modifier
            .align(Alignment.End)
            .clickable { action(SetLoggingEnabled(!state.isLoggingEnabled)) },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = state.isLoggingEnabled,
            onCheckedChange = { action(SetLoggingEnabled(it)) },
        )
        Text(stringResource(string.enable_logging))
    }
}