package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeSearchSuggestionsEnabled

@Composable
fun SettingsGroupSearch(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsGroup(title = "Search") {
        val checked = state.searchSuggestionsEnabled
        SettingsCheckBox(
            text = "Enable suggestions",
            icon = when {
                checked -> R.drawable.ic_lightbulb
                else -> R.drawable.ic_lightbulb_off
            },
            isChecked = checked,
            onCheckedChange = { action(ChangeSearchSuggestionsEnabled(it)) }
        )
    }
}