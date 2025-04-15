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

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.minCacheSize
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeCache
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ClearCache
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.CacheState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.slider.UhuruSliderRow
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.changes_effect_after_restart
import uhuruphotos_android.foundation.strings.api.generated.resources.clear
import uhuruphotos_android.foundation.strings.api.generated.resources.currently_used
import uhuruphotos_android.foundation.strings.api.generated.resources.max_limit
import kotlin.math.max

@Composable
internal fun SettingsCache(
    cacheState: CacheState,
    action: (SettingsAction) -> Unit,
) {
    val initialMaxLimit = cacheState.max.toFloat()
    SettingsTextButtonRow(
        text = stringResource(string.currently_used, cacheState.current),
        buttonText = stringResource(string.clear),
        onClick = { action(ClearCache(cacheState.cacheType)) }
    )
    HorizontalDivider()
    UhuruSliderRow(
        text = { stringResource(string.max_limit, it.toInt()) },
        subtext = string.changes_effect_after_restart,
        initialValue = initialMaxLimit,
        range = (minCacheSize.toFloat().. cacheState.limit.toFloat()).maybeExpandTo(initialMaxLimit),
        onValueChanged = { action(ChangeCache(cacheState.cacheType, it)) }
    )
}

private fun ClosedFloatingPointRange<Float>.maybeExpandTo(end: Float) =
    start..max(endInclusive, end)
