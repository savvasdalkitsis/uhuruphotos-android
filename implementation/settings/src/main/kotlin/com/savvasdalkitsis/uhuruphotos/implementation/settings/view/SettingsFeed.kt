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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.FeedRefreshChanged
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.api.strings.R as Strings

@Composable
internal fun ColumnScope.SettingsFeed(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    val daysUpperLimit = 31
    SettingsSliderRow(
        text = {
            val days = pluralStringResource(Strings.plurals.days, it.toInt(), it.toInt())
            "${stringResource(Strings.string.feed_refresh_days)}: $days"
        },
        initialValue = state.feedDaysToRefresh.toFloat(),
        range = 1f..daysUpperLimit.toFloat(),
        steps = daysUpperLimit,
        onValueChanged = { action(FeedRefreshChanged(it.toInt())) }
    )

    val days = 7
    val initialValue = (state.feedSyncFrequency ?: 0).toFloat()
    val frequencyUpperLimit = days * 24f
    SettingsSliderRow(
        text = {
            val frequency = when (it) {
                frequencyUpperLimit -> stringResource(Strings.string.never)
                else -> pluralStringResource(Strings.plurals.hours, it.toInt(), it.toInt())
            }
            "${stringResource(Strings.string.feed_sync_freq)}: $frequency"
        },
        initialValue = initialValue,
        range = 1f..frequencyUpperLimit,
        steps = frequencyUpperLimit.toInt(),
        onValueChanged = { action(SettingsAction.FeedSyncFrequencyChanged(it, frequencyUpperLimit)) }
    )
    Divider()
    SettingsCheckBox(
        text = stringResource(Strings.string.requires_charging),
        icon = R.drawable.ic_power,
        isChecked = state.fullSyncRequiresCharging,
    ) { selected ->
        action(SettingsAction.ChangeFullSyncChargingRequirements(selected))
    }
    Divider()
    SettingsFullSyncNetworkRequirements(state, action)
    Divider()

    AnimatedContent(targetState = state.fullSyncButtonEnabled) { enabled ->
        when {
            enabled -> SettingsButtonRow(
                buttonText = stringResource(Strings.string.perform_full_sync)
            ) {
                action(SettingsAction.AskForFullFeedSync)
            }
            else -> SettingsProgressIndicator(
                text = stringResource(Strings.string.feed_sync_progress),
                progress = state.fullSyncJobProgress,
            )
        }
    }
}