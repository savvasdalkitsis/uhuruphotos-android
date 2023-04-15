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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeLoggingEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ClearLogFileClicked
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SendFeedbackClicked
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun SettingsFeedback(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsOutlineButtonRow(
        buttonText = stringResource(string.send_feedback_with_logs),
        icon = drawable.ic_feedback,
    ) {
        action(SendFeedbackClicked)
    }
    SettingsEntryWithSubtext(subtext = string.degrades_performance) {
        SettingsCheckBox(
            text = stringResource(string.enable_logging),
            icon = drawable.ic_logs,
            isChecked = state.isLoggingEnabled,
            onCheckedChange = { action(ChangeLoggingEnabled(it)) }
        )
    }
    SettingsButtonRow(buttonText = stringResource(string.clear_log_file)) {
        action(ClearLogFileClicked)
    }
}