package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.ui.view.StaggeredGrid
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSize
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSizeClass.*
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
            val columns = when (WindowSize.LOCAL_WIDTH.current) {
                COMPACT -> 1
                MEDIUM -> 2
                EXPANDED -> 3
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