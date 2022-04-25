package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeImageDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearImageDiskCache

@Composable
fun SettingsGroupImageDiskCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroupCache(
        title = "Image Disk cache",
        current = state.imageDiskCacheCurrent,
        initialMaxLimit = state.imageDiskCacheMax.toFloat(),
        clearAction = ClearImageDiskCache,
        changeCacheSizeAction = { ChangeImageDiskCache(it) },
        action = action
    )
}