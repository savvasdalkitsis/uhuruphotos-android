package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction

@Composable
fun SettingsGroupDiskCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Disk cache") {
        SettingsTextButtonRow(
            text = "Currently used: ${state.diskCacheCurrent}mb",
            buttonText = "Clear",
            onClick = { action(SettingsAction.ClearDiskCache) }
        )
        Divider()
        SettingsSliderRow(
            text = { "Max limit: ${it.toInt()}mb" },
            subtext = "(changes will take effect after restart)",
            initialValue = state.diskCacheMax.toFloat(),
            range = 10f..2000f,
            onValueChanged = { action(SettingsAction.ChangeDiskCache(it)) }
        )
    }
}