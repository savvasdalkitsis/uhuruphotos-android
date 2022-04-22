package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction

@Composable
fun SettingsGroupMemoryCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Memory cache") {
        SettingsTextButtonRow(
            text = "Currently used: ${state.memCacheCurrent}mb",
            buttonText = "Clear",
            onClick = { action(SettingsAction.ClearMemCache) }
        )
        Divider()
        SettingsSliderRow(
            text = "Max limit: ${state.memCacheMax}mb",
            subtext = "(changes will take effect after restart)",
            value = state.memCacheMax.toFloat(),
            range = 10f..2000f,
            onValueChange = { action(SettingsAction.ChangeMemCache(it)) }
        )
    }
}