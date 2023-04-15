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

import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.Avatar
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.DismissPrecacheThumbnailsDialog
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.controller.SettingsViewStateController
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.MapProviderState.Selected
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.BackNavButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.StaggeredGrid
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalWindowSize

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
            Avatar(state = state.avatarState)
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
                    Group(controller.uiFeed) {
                        SettingsFeedUI(state, action)
                    }
                    Group(controller.uiTheme) {
                        SettingsTheme(state, action)
                    }
                    Group(controller.uiSearch) {
                        SettingsSearch(state, action)
                    }
                    Group(controller.uiLibrary) {
                        SettingsLibrary(state, action)
                    }
                    Group(controller.uiVideo) {
                        SettingsVideo(state, action)
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
                    Group(controller.jobsFeedConfiguration) {
                        SettingsJobsFeedConfiguration(state, action)
                    }
                    Group(controller.jobsStatus) {
                        SettingsJobsStatus(state, action)
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
        if (state.showPrecacheThumbnailsDialog) {
            SettingsPrecacheThumbnailsPermissionDialog(action,
                onDismiss = {
                    action(DismissPrecacheThumbnailsDialog)
                }
            )
        }
    }
}