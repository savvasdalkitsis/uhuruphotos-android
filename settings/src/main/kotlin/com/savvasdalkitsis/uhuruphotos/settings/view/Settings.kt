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
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.ui.view.StaggeredGrid
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.UserBadge

@Composable
fun Settings(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    CommonScaffold(
        title = { Text(text = "Settings") },
        actionBarContent = {
            UserBadge(state = state.userInformationState)
        },
        navigationIcon = { BackNavButton {
            action(NavigateBack)
        }}
    ) { contentPadding ->
        if (state.isLoading) {
            FullProgressBar()
        } else {
            val columns = when (LocalWindowSize.current.widthSizeClass) {
                Compact -> 1
                Medium -> 2
                else -> 3
            }
            StaggeredGrid(
                contentPadding = contentPadding,
                syncScrolling = false,
                columnCount = columns,
            ) {
                item {
                    SettingsGroupTheme(state, action)
                }
                item {
                    SettingsGroupImageDiskCache(state, action)
                }
                item {
                    SettingsGroupImageMemoryCache(state, action)
                }
                item {
                    SettingsGroupVideoDiskCache(state, action)
                }
                item {
                    SettingsGroupJobs(state, action)
                }
                item {
                    SettingsGroupSearch(state, action)
                }
                item {
                    SettingsGroupShare(state, action)
                }
                item {
                    SettingsGroupFeedback(action)
                }
            }
        }
        if (state.showFullFeedSyncDialog) {
            SettingsFullFeedSyncPermissionDialog(action,
                onDismiss = {
                    action(DismissFullFeedSyncDialog)
                }
            )
        }
    }
}