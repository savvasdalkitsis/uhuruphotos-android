package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.material.Divider
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
    SettingsGroup(title = "Video Disk cache") {
        SettingsTextButtonRow(
            text = "Currently used: ${state.videoDiskCacheCurrent}mb",
            buttonText = "Clear",
            onClick = { action(ClearVideoDiskCache) }
        )
        Divider()
        SettingsSliderRow(
            text = { "Max limit: ${it.toInt()}mb" },
            subtext = "(changes will take effect after restart)",
            initialValue = state.videoDiskCacheMax.toFloat(),
            range = 10f..2000f,
            onValueChanged = { action(ChangeVideoDiskCache(it)) }
        )
    }
}