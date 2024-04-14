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

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.ALWAYS_OFF
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.ALWAYS_ON
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.SHOW_ON_SCROLL
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeAutoHideNavOnScrollEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeFeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeMemoriesEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun SettingsFeedUI(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsCheckBox(
        text = stringResource(string.enable_memories),
        icon = drawable.ic_thought_bubble,
        isChecked = state.showMemories,
    ) {
        action(ChangeMemoriesEnabled(!state.showMemories))
    }
    SettingsCheckBox(
        text = stringResource(string.auto_hide_nav_on_scroll),
        icon = drawable.ic_swipe_vertical,
        isChecked = state.autoHideFeedNavOnScroll,
    ) {
        action(ChangeAutoHideNavOnScrollEnabled(!state.autoHideFeedNavOnScroll))
    }
    if (state.hasRemoteAccess) {
        Divider()
        SettingsTextRow(stringResource(string.show_media_sync_status))
        SettingsTextDropDownButtonRow(
            content = {
                SyncDisplayRow(state.feedMediaItemSyncDisplay)
            },
            buttonText = stringResource(string.change),
            action = action,
        ) {
            @Composable
            fun item(display: FeedMediaItemSyncDisplay) {
                Item({ SyncDisplayRow(display) }, ChangeFeedMediaItemSyncDisplay(display))
            }
            item(SHOW_ON_SCROLL)
            item(ALWAYS_OFF)
            item(ALWAYS_ON)
        }
    }
}