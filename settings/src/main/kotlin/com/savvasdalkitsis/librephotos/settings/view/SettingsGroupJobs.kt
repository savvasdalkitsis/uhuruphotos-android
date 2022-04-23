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
        val upperLimit = days * 24f
        SettingsSliderRow(
            text = {
                val frequency = when (it) {
                    upperLimit -> "never"
                    else -> "${it.toInt()} hour(s)"
                }
                "Full photo feed sync frequency: $frequency"
            },
            initialValue = initialValue,
            range = 1f..upperLimit,
            steps = upperLimit.toInt(),
            onValueChanged = { action(FeedSyncFrequencyChanged(it, upperLimit)) }
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