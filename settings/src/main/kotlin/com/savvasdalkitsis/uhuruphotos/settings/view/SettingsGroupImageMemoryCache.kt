package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeImageMemCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearImageMemCache

@Composable
fun SettingsGroupImageMemoryCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroupCache(
        title = "Image Memory cache",
        current = state.imageMemCacheCurrent,
        initialMaxLimit = state.imageMemCacheMax.toFloat(),
        clearAction = ClearImageMemCache,
        changeCacheSizeAction = { ChangeImageMemCache(it) },
        action = action,
    )
}