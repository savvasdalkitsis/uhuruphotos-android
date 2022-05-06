package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearLogFileClicked
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.SendFeedbackClicked

@Composable
fun SettingsGroupFeedback(
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Feedback") {
        SettingsOutlineButtonRow(
            buttonText = "Send feedback with logs",
            icon = R.drawable.ic_feedback,
        ) {
            action(SendFeedbackClicked)
        }
        SettingsButtonRow(buttonText = "Clear log file") {
            action(ClearLogFileClicked)
        }
    }
}