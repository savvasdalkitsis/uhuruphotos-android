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
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeShouldShowFeedDetailsSyncProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeShouldShowFullSyncProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeShouldShowLocalProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeShouldShowPrecacheProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun SettingsProgressUI(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    if (state.hasRemoteAccess) {
        SettingsCheckBox(
            text = stringResource(string.show_feed_sync_progress),
            icon = drawable.ic_feed,
            isChecked = state.shouldShowFeedSyncProgress,
        ) {
            action(ChangeShouldShowFullSyncProgress(!state.shouldShowFeedSyncProgress))
        }
        SettingsCheckBox(
            text = stringResource(string.show_feed_details_sync_progress),
            icon = drawable.ic_feed,
            isChecked = state.shouldShowFeedDetailsSyncProgress,
        ) {
            action(ChangeShouldShowFeedDetailsSyncProgress(!state.shouldShowFeedDetailsSyncProgress))
        }
        SettingsCheckBox(
            text = stringResource(string.show_precache_progress),
            icon = drawable.ic_photo,
            isChecked = state.shouldShowPrecacheProgress,
        ) {
            action(ChangeShouldShowPrecacheProgress(!state.shouldShowPrecacheProgress))
        }
    }
    SettingsCheckBox(
        text = stringResource(string.show_local_progress),
        icon = drawable.ic_folder,
        isChecked = state.shouldShowLocalSyncProgress,
    ) {
        action(ChangeShouldShowLocalProgress(!state.shouldShowLocalSyncProgress))
    }
}