package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeShareGpsDataEnabled

@Composable
fun SettingsGroupShare(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsGroup(title = "Share") {
        val checked = state.shareRemoveGpsDataEnabled
        SettingsCheckBox(
            text = "Remove GPS data when sharing",
            icon = when {
                checked -> R.drawable.ic_gps_off
                else -> R.drawable.ic_gps_on
            },
            isChecked = checked,
            onCheckedChange = { action(ChangeShareGpsDataEnabled(it)) }
        )
    }
}