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
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.api.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.api.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.api.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.api.ui.view.StaggeredGrid
import com.savvasdalkitsis.uhuruphotos.api.ui.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.UserBadge
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.controller.SettingsViewStateController
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.MapProviderState.Selected
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState

@Composable
internal fun Settings(
    controller: SettingsViewStateController,
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    CommonScaffold(
        title = { Text(stringResource(string.settings)) },
        actionBarContent = {
            ActionIcon(
                onClick = { controller.collapseAll() },
                icon = drawable.ic_expand_all,
                contentDescription = stringResource(string.expand_all),
            )
            ActionIcon(
                onClick = { controller.expandAll() },
                icon = drawable.ic_collapse_all,
                contentDescription = stringResource(string.collapse_all),
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
                SuperGroup(controller.ui) {
                    Group(controller.uiTheme) {
                        SettingsTheme(state, action)
                    }
                    Group(controller.uiSearch) {
                        SettingsSearch(state, action)
                    }
                    Group(controller.uiLibrary) {
                        SettingsLibrary(state, action)
                    }
                    val mapProvider = state.mapProviderState
                    if (mapProvider is Selected) {
                        Group(controller.uiMaps) {
                            SettingsMaps(mapProvider, action)
                        }
                    }
                }
                SuperGroup(controller.privacy) {
                    state.biometrics?.let {
                        Group(controller.privacyBiometrics) {
                            SettingsBiometrics(it, action)
                        }
                    }
                    Group(controller.privacyShare) {
                        SettingsShare(state, action)
                    }
                }
                SuperGroup(controller.jobs) {
                    Group(controller.jobsFeed) {
                        SettingsFeed(state, action)
                    }
                }
                SuperGroup(controller.advanced) {
                    Group(controller.advancedImageDiskCache) {
                        SettingsImageDiskCache(state, action)
                    }
                    Group(controller.advancedImageMemoryCache) {
                        SettingsImageMemoryCache(state, action)
                    }
                    Group(controller.advancedVideoDiskCache) {
                        SettingsVideoDiskCache(state, action)
                    }
                }
                SuperGroup(controller.help) {
                    Group(controller.helpFeedback) {
                        SettingsFeedback(state, action)
                    }
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