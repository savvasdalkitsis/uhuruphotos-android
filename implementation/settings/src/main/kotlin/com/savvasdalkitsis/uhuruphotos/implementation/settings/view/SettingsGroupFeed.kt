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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.FeedRefreshChanged
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.api.strings.R as Strings

@Composable
internal fun SettingsGroupFeed(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
    collapsed: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    SettingsGroup(
        title = stringResource(Strings.string.feed),
        collapsed = collapsed,
    ) {
        val upperLimit = 31
        SettingsSliderRow(
            text = {
                val days = pluralStringResource(Strings.plurals.days, it.toInt(), it.toInt())
                "${stringResource(Strings.string.feed_refresh_days)}: $days"
            },
            initialValue = state.feedDaysToRefresh.toFloat(),
            range = 1f..upperLimit.toFloat(),
            steps = upperLimit,
            onValueChanged = { action(FeedRefreshChanged(it.toInt())) }
        )
    }
}