package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.ChangeVideoDiskCache
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.ClearVideoDiskCache

@Composable
fun SettingsGroupVideoDiskCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroupCache(
        title = "Video Disk cache",
        current = state.videoDiskCacheCurrent,
        initialMaxLimit = state.videoDiskCacheMax.toFloat(),
        clearAction = ClearVideoDiskCache,
        changeCacheSizeAction = { ChangeVideoDiskCache(it) },
        action = action,
    )
}