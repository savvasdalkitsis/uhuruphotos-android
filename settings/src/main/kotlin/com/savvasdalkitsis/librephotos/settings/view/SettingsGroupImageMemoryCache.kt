package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction

@Composable
fun SettingsGroupImageMemoryCache(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Image Memory cache") {
        SettingsTextButtonRow(
            text = "Currently used: ${state.imageMemCacheCurrent}mb",
            buttonText = "Clear",
            onClick = { action(SettingsAction.ClearImageMemCache) }
        )
        Divider()
        SettingsSliderRow(
            text = { "Max limit: ${it.toInt()}mb" },
            subtext = "(changes will take effect after restart)",
            initialValue = state.imageMemCacheMax.toFloat(),
            range = 10f..2000f,
            onValueChanged = { action(SettingsAction.ChangeImageMemCache(it)) }
        )
    }
}