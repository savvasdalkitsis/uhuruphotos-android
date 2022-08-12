/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.settings.view

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode.DARK_MODE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode.FOLLOW_SYSTEM
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode.LIGHT_MODE
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ChangeThemeMode
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState

@Composable
internal fun ColumnScope.SettingsTheme(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsTextDropDownButtonRow(
        content = {
            ThemeRow(state.themeMode)
        },
        buttonText = stringResource(string.change),
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