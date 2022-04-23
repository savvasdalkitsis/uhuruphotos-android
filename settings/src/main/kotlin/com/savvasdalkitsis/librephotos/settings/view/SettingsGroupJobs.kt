package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
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
        SettingsSliderRow(
            text = "Full photo feed sync frequency: ${state.feedSyncFrequency ?: "-"} hour(s)",
            value = state.feedSyncFrequency?.toFloat(),
            range = 1f..(days * 24f),
            steps = days * 24,
            onValueChange = { action(ChangingFeedSyncFrequency(it)) },
            onValueChangeFinished = { action(FinaliseFeedSyncFrequencyChange) }
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