package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.savvasdalkitsis.librephotos.userbadge.api.view.UserBadge

@Composable
fun Settings(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    CommonScaffold(
        navigationIcon = { BackNavButton {
            action(NavigateBack)
        }},
        actionBarContent = {
            UserBadge(state = state.userInformationState)
        },
        title = { Text(text = "Settings") }
    ) { contentPadding ->
        if (state.isLoading) {
            FullProgressBar()
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = contentPadding.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsGroupDiskCache(state, action)
                SettingsGroupMemoryCache(state, action)
                SettingsGroupJobs(state, action)
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