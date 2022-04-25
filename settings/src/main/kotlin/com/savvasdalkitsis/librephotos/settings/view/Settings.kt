package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.*
import com.savvasdalkitsis.librephotos.ui.view.BackNavButton
import com.savvasdalkitsis.librephotos.ui.view.CommonScaffold
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar
import com.savvasdalkitsis.librephotos.ui.window.WindowSize
import com.savvasdalkitsis.librephotos.ui.window.WindowSizeClass.*
import com.savvasdalkitsis.librephotos.userbadge.api.view.UserBadge

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
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                columns = GridCells.Fixed(columns),
            ) {
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