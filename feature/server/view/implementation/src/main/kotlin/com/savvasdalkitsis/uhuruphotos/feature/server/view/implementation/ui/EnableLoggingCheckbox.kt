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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.SetLoggingEnabled
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.ToggleableButtonWithIcon
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_logs
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.enable_logging

@Composable
internal fun EnableLoggingCheckbox(
    state: ServerState,
    action: (ServerAction) -> Unit
) {
    ToggleableButtonWithIcon(
        modifier = Modifier
            .padding(vertical = 4.dp),
        icon = drawable.ic_logs,
        text = stringResource(string.enable_logging),
        checked = state.isLoggingEnabled,
    ) {
        action(SetLoggingEnabled(it))
    }
}