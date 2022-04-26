package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeThemeMode
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode.*

@Composable
fun SettingsGroupTheme(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsGroup(title = "Theme") {
        SettingsTextDropDownButtonRow(
            content = {
                ThemeRow(state.themeMode)
            },
            buttonText = "Change",
            action = action,
        ) {
            @Composable
            fun item(themeMode: ThemeMode) {
                Item({ ThemeRow(themeMode) }, ChangeThemeMode(themeMode))
            }
            item(FOLLOW_SYSTEM)
            item(DARK_MODE)
            item(LIGHT_MODE)
        }
    }
}