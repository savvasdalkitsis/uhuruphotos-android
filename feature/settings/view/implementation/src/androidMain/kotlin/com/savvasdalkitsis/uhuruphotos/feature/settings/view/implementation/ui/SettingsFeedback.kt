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
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeLoggingEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeSendDatabaseEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ClearLogFileClicked
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SendFeedbackClicked
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ViewLogsClicked
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun SettingsFeedback(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsOutlineButtonRow(
        buttonText = stringResource(strings.view_logs),
        icon = images.ic_logs,
    ) {
        action(ViewLogsClicked)
    }
    SettingsOutlineButtonRow(
        buttonText = stringResource(strings.send_feedback_with_logs),
        icon = images.ic_feedback,
    ) {
        action(SendFeedbackClicked)
    }
    SettingsCheckBox(
        text = stringResource(strings.send_database),
        icon = images.ic_database_send,
        isChecked = state.isSendDatabaseEnabled,
        onCheckedChange = { action(ChangeSendDatabaseEnabled(it)) }
    )
    SettingsEntryWithSubtext(subtext = strings.degrades_performance) {
        SettingsCheckBox(
            text = stringResource(strings.enable_logging),
            icon = images.ic_logs,
            isChecked = state.isLoggingEnabled,
            onCheckedChange = { action(ChangeLoggingEnabled(it)) }
        )
    }
    SettingsButtonRow(buttonText = stringResource(strings.clear_log_file)) {
        action(ClearLogFileClicked)
    }
}