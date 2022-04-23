package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.ChangeImageMemCache
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.ClearImageMemCache

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