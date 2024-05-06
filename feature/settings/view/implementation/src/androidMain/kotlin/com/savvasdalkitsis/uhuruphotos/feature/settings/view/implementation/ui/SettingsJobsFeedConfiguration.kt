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

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeFullSyncChargingRequirements
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.FeedRefreshChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.FeedSyncFrequencyChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.plurals
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.compose.pluralStringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun SettingsJobsFeedConfiguration(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    val daysUpperLimit = 31
    SettingsSliderRow(
        text = {
            val days = pluralStringResource(plurals.days, it.toInt(), it.toInt())
            "${stringResource(strings.feed_refresh_days)}: $days"
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
                frequencyUpperLimit -> stringResource(strings.never)
                else -> pluralStringResource(plurals.hours, it.toInt(), it.toInt())
            }
            "${stringResource(strings.feed_sync_freq)}: $frequency"
        },
        initialValue = initialValue,
        range = 1f..frequencyUpperLimit,
        steps = frequencyUpperLimit.toInt(),
        onValueChanged = { action(FeedSyncFrequencyChanged(it, frequencyUpperLimit)) }
    )
    Divider()
    SettingsCheckBox(
        text = stringResource(strings.requires_charging),
        icon = images.ic_power,
        isChecked = state.fullSyncRequiresCharging,
    ) { selected ->
        action(ChangeFullSyncChargingRequirements(selected))
    }
    Divider()
    SettingsNetworkRequirements(state.fullSyncNetworkRequirement, action) {
        ChangeFullSyncNetworkRequirements(it)
    }
}