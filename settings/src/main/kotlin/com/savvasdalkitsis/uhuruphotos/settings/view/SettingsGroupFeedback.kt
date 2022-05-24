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
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.icons.R
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