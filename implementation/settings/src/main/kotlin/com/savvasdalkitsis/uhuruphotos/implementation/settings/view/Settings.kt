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

import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.api.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.api.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.api.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.api.ui.view.StaggeredGrid
import com.savvasdalkitsis.uhuruphotos.api.ui.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.UserBadge
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.MapProviderState.*
import com.savvasdalkitsis.uhuruphotos.api.icons.R as Icons

@Composable
internal fun Settings(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    val collapsedTheme = remember { mutableStateOf(false) }
    val collapsedImageDiskCache = remember { mutableStateOf(false) }
    val collapsedImageMemoryCache = remember { mutableStateOf(false) }
    val collapsedVideoDiskCache = remember { mutableStateOf(false) }
    val collapsedJobs = remember { mutableStateOf(false) }
    val collapsedSearch = remember { mutableStateOf(false) }
    val collapsedShare = remember { mutableStateOf(false) }
    val collapsedLibrary = remember { mutableStateOf(false) }
    val collapsedFeedback = remember { mutableStateOf(false) }
    val collapsedMap = remember { mutableStateOf(false) }
    val allCollapse = listOf(
        collapsedTheme,
        collapsedImageDiskCache,
        collapsedImageMemoryCache,
        collapsedVideoDiskCache,
        collapsedJobs,
        collapsedSearch,
        collapsedShare,
        collapsedLibrary,
        collapsedFeedback,
        collapsedMap,
    )
    CommonScaffold(
        title = { Text(stringResource(R.string.settings)) },
        actionBarContent = {
            ActionIcon(
                onClick = { allCollapse.forEach { it.value = false } },
                icon = Icons.drawable.ic_expand_all,
                contentDescription = stringResource(R.string.expand_all),
            )
            ActionIcon(
                onClick = { allCollapse.forEach { it.value = true } },
                icon = Icons.drawable.ic_collapse_all,
                contentDescription = stringResource(R.string.collapse_all),
            )
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
                    SettingsGroupTheme(state, action, collapsedTheme)
                }
                item {
                    SettingsGroupImageDiskCache(state, action, collapsedImageDiskCache)
                }
                item {
                    SettingsGroupImageMemoryCache(state, action, collapsedImageMemoryCache)
                }
                item {
                    SettingsGroupVideoDiskCache(state, action, collapsedVideoDiskCache)
                }
                item {
                    SettingsGroupJobs(state, action, collapsedJobs)
                }
                item {
                    SettingsGroupSearch(state, action, collapsedSearch)
                }
                item {
                    SettingsGroupShare(state, action, collapsedShare)
                }
                item {
                    SettingsGroupLibrary(state, action, collapsedLibrary)
                }
                when (val mapProviderState = state.mapProviderState) {
                    is Selected -> item {
                        SettingsGroupMaps(mapProviderState, action)
                    }
                }
                item {
                    SettingsGroupFeedback(action, collapsedFeedback)
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