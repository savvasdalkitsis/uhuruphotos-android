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

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.AskForFullFeedSync
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeFullSyncChargingRequirements
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.FeedSyncFrequencyChanged
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.api.icons.R as Icons
import com.savvasdalkitsis.uhuruphotos.strings.R as Strings

@Composable
internal fun SettingsGroupJobs(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
    collapsed: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    SettingsGroup(
        title = stringResource(Strings.string.jobs),
        collapsed = collapsed,
    ) {
        val days = 7
        val initialValue = (state.feedSyncFrequency ?: 0).toFloat()
        val upperLimit = days * 24f
        SettingsSliderRow(
            text = {
                val frequency = when (it) {
                    upperLimit -> stringResource(Strings.string.never)
                    else -> pluralStringResource(Strings.plurals.hours, it.toInt(), it.toInt())
                }
                "${stringResource(Strings.string.feed_sync_freq)}: $frequency"
            },
            initialValue = initialValue,
            range = 1f..upperLimit,
            steps = upperLimit.toInt(),
            onValueChanged = { action(FeedSyncFrequencyChanged(it, upperLimit)) }
        )
        Divider()
        SettingsCheckBox(
            text = stringResource(Strings.string.requires_charging),
            icon = Icons.drawable.ic_power,
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
                    buttonText = stringResource(Strings.string.perform_full_sync)
                ) {
                    action(AskForFullFeedSync)
                }
                else -> SettingsProgressIndicator(
                    text = stringResource(Strings.string.feed_sync_progress),
                    progress = state.fullSyncJobProgress,
                )
            }
        }

    }
}