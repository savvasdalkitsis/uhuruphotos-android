package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction

@Composable
fun SettingsGroupCache(
    title: String,
    current: Int,
    initialMaxLimit: Float,
    range: ClosedFloatingPointRange<Float> = 10f..2000f,
    clearAction: SettingsAction,
    changeCacheSizeAction: (Float) -> SettingsAction,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = title) {
        SettingsTextButtonRow(
            text = "Currently used: ${current}mb",
            buttonText = "Clear",
            onClick = { action(clearAction) }
        )
        Divider()
        SettingsSliderRow(
            text = { "Max limit: ${it.toInt()}mb" },
            subtext = "(changes will take effect after restart)",
            initialValue = initialMaxLimit,
            range = range,
            onValueChanged = { action(changeCacheSizeAction(it)) }
        )
    }
}