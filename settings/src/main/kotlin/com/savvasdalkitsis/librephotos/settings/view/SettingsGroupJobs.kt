package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.material.Divider
import androidx.compose.runtime.*
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.*

@Composable
fun SettingsGroupJobs(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Jobs") {
        val days = 7
        val initialValue  = (state.feedSyncFrequency ?: 0).toFloat()
        SettingsSliderRow(
            text = { "Full photo feed sync frequency: ${it.toInt()} hour(s)" },
            initialValue = initialValue,
            range = 1f..(days * 24f),
            steps = days * 24,
            onValueChanged = { action(FeedSyncFrequencyChanged(it)) }
        )
        Divider()
        SettingsCheckBox(
            text = "Requires charging",
            icon = R.drawable.ic_power,
            isChecked = state.fullSyncRequiresCharging,
        ) { selected ->
            action(ChangeFullSyncChargingRequirements(selected))
        }
        Divider()
        SettingsFullSyncNetworkRequirements(state, action)
        Divider()
        SettingsButtonRow(
            enabled = state.fullSyncButtonEnabled,
            buttonText = "Perform full sync now"
        ) {
            action(AskForFullFeedSync)
        }
    }
}