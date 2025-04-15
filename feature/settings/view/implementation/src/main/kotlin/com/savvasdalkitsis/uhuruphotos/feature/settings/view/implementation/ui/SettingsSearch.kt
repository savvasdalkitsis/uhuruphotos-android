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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeSearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ClearRecentSearches
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox.UhuruCheckBoxRow
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.clear_recent_searches
import uhuruphotos_android.foundation.strings.api.generated.resources.enable_suggestions

@Composable
internal fun SettingsSearch(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    val checked = state.searchSuggestionsEnabled
    UhuruCheckBoxRow(
        text = stringResource(string.enable_suggestions),
        icon = when {
            checked -> drawable.ic_lightbulb
            else -> drawable.ic_lightbulb_off
        },
        isChecked = checked,
        onCheckedChange = { action(ChangeSearchSuggestionsEnabled(it)) }
    )
    SettingsOutlineButtonRow(
        buttonText = stringResource(string.clear_recent_searches),
        icon = drawable.ic_clear_all,
    ) {
        action(ClearRecentSearches)
    }
}