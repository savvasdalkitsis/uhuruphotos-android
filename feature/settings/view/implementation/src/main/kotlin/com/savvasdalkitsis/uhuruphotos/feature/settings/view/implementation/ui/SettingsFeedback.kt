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
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox.UhuruCheckBoxRow
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.layout.UhuruEntryWithSubtext
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.clear_log_file
import uhuruphotos_android.foundation.strings.api.generated.resources.degrades_performance
import uhuruphotos_android.foundation.strings.api.generated.resources.enable_logging
import uhuruphotos_android.foundation.strings.api.generated.resources.send_database
import uhuruphotos_android.foundation.strings.api.generated.resources.send_feedback_with_logs
import uhuruphotos_android.foundation.strings.api.generated.resources.view_logs

@Composable
internal fun SettingsFeedback(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsOutlineButtonRow(
        buttonText = stringResource(string.view_logs),
        icon = drawable.ic_logs,
    ) {
        action(ViewLogsClicked)
    }
    SettingsOutlineButtonRow(
        buttonText = stringResource(string.send_feedback_with_logs),
        icon = drawable.ic_feedback,
    ) {
        action(SendFeedbackClicked)
    }
    UhuruCheckBoxRow(
        text = stringResource(string.send_database),
        icon = drawable.ic_database_send,
        isChecked = state.isSendDatabaseEnabled,
        onCheckedChange = { action(ChangeSendDatabaseEnabled(it)) }
    )
    UhuruEntryWithSubtext(subtext = string.degrades_performance) {
        UhuruCheckBoxRow(
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