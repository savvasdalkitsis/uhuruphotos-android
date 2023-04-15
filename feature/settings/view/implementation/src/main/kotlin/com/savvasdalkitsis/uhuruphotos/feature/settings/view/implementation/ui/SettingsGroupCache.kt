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
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlin.math.max

@Composable
internal fun SettingsGroupCache(
    current: Int,
    initialMaxLimit: Float,
    range: ClosedFloatingPointRange<Float> = 10f..2000f,
    clearAction: SettingsAction,
    changeCacheSizeAction: (Float) -> SettingsAction,
    action: (SettingsAction) -> Unit,
) {
    SettingsTextButtonRow(
        text = stringResource(string.currently_used, current),
        buttonText = stringResource(string.clear),
        onClick = { action(clearAction) }
    )
    Divider()
    SettingsSliderRow(
        text = { stringResource(string.max_limit, it.toInt()) },
        subtext = string.changes_effect_after_restart,
        initialValue = initialMaxLimit,
        range = range.maybeExpandTo(initialMaxLimit),
        onValueChanged = { action(changeCacheSizeAction(it)) }
    )
}

private fun ClosedFloatingPointRange<Float>.maybeExpandTo(end: Float) =
    start..max(endInclusive, end)
