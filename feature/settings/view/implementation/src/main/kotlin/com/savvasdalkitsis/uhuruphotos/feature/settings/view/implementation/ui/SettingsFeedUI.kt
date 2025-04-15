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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState.ALWAYS_OFF
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState.ALWAYS_ON
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState.SHOW_ON_SCROLL
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeAutoHideNavOnScrollEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeFeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeMemoriesEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeMemoriesParallaxEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox.UhuruCheckBoxRow
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.UhuruTextRow
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.auto_hide_nav_on_scroll
import uhuruphotos_android.foundation.strings.api.generated.resources.enable_memories
import uhuruphotos_android.foundation.strings.api.generated.resources.memories_parallax
import uhuruphotos_android.foundation.strings.api.generated.resources.show_media_sync_status

@Composable
internal fun ColumnScope.SettingsFeedUI(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    UhuruCheckBoxRow(
        text = stringResource(string.enable_memories),
        icon = drawable.ic_thought_bubble,
        isChecked = state.showMemories,
    ) {
        action(ChangeMemoriesEnabled(!state.showMemories))
    }
    UhuruCheckBoxRow(
        text = stringResource(string.memories_parallax),
        icon = drawable.ic_page_sidebar_left,
        isChecked = state.memoriesParallax,
    ) {
        action(ChangeMemoriesParallaxEnabled(!state.memoriesParallax))
    }
    UhuruCheckBoxRow(
        text = stringResource(string.auto_hide_nav_on_scroll),
        icon = drawable.ic_swipe_vertical,
        isChecked = state.autoHideFeedNavOnScroll,
    ) {
        action(ChangeAutoHideNavOnScrollEnabled(!state.autoHideFeedNavOnScroll))
    }
    if (state.hasRemoteAccess) {
        HorizontalDivider()
        UhuruTextRow(stringResource(string.show_media_sync_status))
        BottomAppBar {
            BottomItem(SHOW_ON_SCROLL, state, action)
            BottomItem(ALWAYS_OFF, state, action)
            BottomItem(ALWAYS_ON, state, action)
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    expectedState: FeedMediaItemSyncDisplayState,
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    NavigationBarItem(
        label = { Text(stringResource(expectedState.friendlyName)) },
        icon = {
            UhuruIcon(icon = expectedState.icon)
        },
        selected = state.feedMediaItemSyncDisplayState == expectedState,
        onClick = { action(ChangeFeedMediaItemSyncDisplay(expectedState)) }
    )
}

@Preview
@Composable
private fun SettingsFeedUIPreview() {
    PreviewAppTheme {
        Column {
            SettingsFeedUI(
                state = SettingsState(isLoading = false, hasRemoteAccess = true)
            ) {}
        }
    }
}


@Preview
@Composable
private fun SettingsFeedUIPreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        Column {
            SettingsFeedUI(
                state = SettingsState(isLoading = false, hasRemoteAccess = true)
            ) {}
        }
    }
}
