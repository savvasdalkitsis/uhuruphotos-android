package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.icons.R
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painter = painterResource(state.themeMode.icon), contentDescription = null)
                    Text(state.themeMode.friendlyName)
                }
            },
            buttonText = "Change",
            action = action,
        ) {
            @Composable
            fun item(themeMode: ThemeMode) {
                Item(themeMode.friendlyName, ChangeThemeMode(themeMode))
            }
            item(FOLLOW_SYSTEM)
            item(DARK_MODE)
            item(LIGHT_MODE)
        }
    }
}