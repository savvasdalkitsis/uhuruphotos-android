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

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeImageMemCache
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ClearImageMemCache
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState


@Composable
internal fun SettingsImageMemoryCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsGroupCache(
        current = state.imageMemCacheCurrent,
        initialMaxLimit = state.imageMemCacheMax.toFloat(),
        range = 10f.. state.imageMemCacheLimit.toFloat(),
        clearAction = ClearImageMemCache,
        changeCacheSizeAction = { ChangeImageMemCache(it) },
        action = action,
    )
}