package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.*

@Composable
fun SettingsGroupJobs(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Jobs") {
        val days = 7
        val initialValue = (state.feedSyncFrequency ?: 0).toFloat()
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

        AnimatedContent(targetState = state.fullSyncButtonEnabled) { enabled ->
            when {
                enabled -> SettingsButtonRow(
                    buttonText = "Perform full sync now"
                ) {
                    action(AskForFullFeedSync)
                }
                else -> SettingsProgressIndicator(
                    text = "Full photo feed sync progress",
                    progress = state.fullSyncJobProgress,
                )
            }
        }

    }
}