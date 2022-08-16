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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ChangeVideoDiskCache
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ClearVideoDiskCache
import com.savvasdalkitsis.uhuruphotos.implementation.settings.ui.state.SettingsState

@Composable
internal fun ColumnScope.SettingsVideoDiskCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsGroupCache(
        current = state.videoDiskCacheCurrent,
        initialMaxLimit = state.videoDiskCacheMax.toFloat(),
        clearAction = ClearVideoDiskCache,
        changeCacheSizeAction = { ChangeVideoDiskCache(it) },
        action = action,
    )
}