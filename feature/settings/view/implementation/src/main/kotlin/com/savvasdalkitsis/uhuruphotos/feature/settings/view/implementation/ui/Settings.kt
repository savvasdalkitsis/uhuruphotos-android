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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.Avatar
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.JobPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.AboutPressed
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.DismissJobDialog
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.StartJob
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.controller.SettingsViewStateController
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.MapProviderState.Selected
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.StaggeredGrid
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

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
        navigationIcon = { UpNavButton() }
    ) { contentPadding ->
        if (state.isLoading) {
            FullLoading()
        } else {
            val columns = when (LocalWindowSize.current.widthSizeClass) {
                Compact -> 1
                Medium -> 2
                else -> 3
            }
            StaggeredGrid(
                modifier = Modifier.padding(horizontal = 8.dp),
                contentPadding = contentPadding,
                syncScrolling = false,
                itemSpacing = 8.dp,
                columnCount = columns,
            ) {
                SuperGroup(controller.ui) {
                    CollapsibleGroup(groupState = controller.uiFeed) {
                        SettingsFeedUI(state, action)
                    }
                    CollapsibleGroup(groupState = controller.uiTheme) {
                        SettingsTheme(state, action)
                    }
                    CollapsibleGroup(groupState = controller.uiProgress) {
                        SettingsProgressUI(state, action)
                    }
                    if (state.hasRemoteAccess) {
                        CollapsibleGroup(groupState = controller.uiSearch) {
                            SettingsSearch(state, action)
                        }
                    }
                    CollapsibleGroup(groupState = controller.uiLibrary) {
                        SettingsLibrary(state, action)
                    }
//                    Group(controller.uiVideo) {
//                        SettingsVideo(state, action)
//                    }
                    val mapProvider = state.mapProviderState
                    if (mapProvider is Selected) {
                        CollapsibleGroup(groupState = controller.uiMaps) {
                            SettingsMaps(mapProvider, action)
                        }
                    }
                }
                SuperGroup(controller.privacy) {
                    state.biometrics?.let {
                        CollapsibleGroup(groupState = controller.privacyBiometrics) {
                            SettingsBiometrics(it, action)
                        }
                    }
                    CollapsibleGroup(groupState = controller.privacyShare) {
                        SettingsShare(state, action)
                    }
                }
                SuperGroup(controller.jobs) {
                    if (state.hasRemoteAccess) {
                        CollapsibleGroup(groupState = controller.jobsFeedConfiguration) {
                            SettingsJobsFeedConfiguration(state, action)
                        }
                    }
                    CollapsibleGroup(groupState = controller.jobsStatus) {
                        SettingsJobsStatus(state, action)
                    }
                }
                SuperGroup(controller.advanced) {
                    CollapsibleGroup(groupState = controller.advancedLightboxPhotoDiskCache) {
                        SettingsCache(state.lightboxPhotoDiskCache, action)
                    }
                    CollapsibleGroup(groupState = controller.advancedLightboxPhotoMemoryCache) {
                        SettingsCache(state.lightboxPhotoMemCache, action)
                    }
                    CollapsibleGroup(groupState = controller.advancedThumbnailDiskCache) {
                        SettingsCache(state.thumbnailDiskCache, action)
                    }
                    CollapsibleGroup(groupState = controller.advancedThumbnailMemoryCache) {
                        SettingsCache(state.thumbnailMemCache, action)
                    }
                    CollapsibleGroup(groupState = controller.advancedVideoDiskCache) {
                        SettingsCache(state.videoDiskCache, action)
                    }
                }
                SuperGroup(controller.help) {
                    CollapsibleGroup(groupState = controller.helpFeedback) {
                        SettingsFeedback(state, action)
                    }
                    SettingsOutlineButtonRow(
                        buttonText = stringResource(string.about),
                        icon = drawable.ic_info,
                    ) {
                        action(AboutPressed)
                    }
                }
            }
        }
        state.showJobStartDialog?.let { job ->
            JobPermissionDialog(
                job = job,
                onStartJob = { action(StartJob(it)) },
                onDismiss = { action(DismissJobDialog) }
            )
        }
    }
}

@Preview
@Composable
internal fun SettingsPreview() {
    PreviewAppTheme {
        Settings(
            controller = SettingsViewStateController(preferences()),
            state = SettingsState(isLoading = false)
        ) {}
    }
}

@Composable
private fun preferences() = object : Preferences {
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = false

    override fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> = flowOf(false)

    override fun setBoolean(key: String, value: Boolean) {
    }

    override fun getInt(key: String, defaultValue: Int): Int = 0

    override fun getNullableInt(key: String, defaultValue: Int?): Int? = 0

    override fun observeInt(key: String, defaultValue: Int): Flow<Int> = flowOf(0)

    override fun setInt(key: String, value: Int) {
    }

    override fun getDouble(key: String, defaultValue: Double): Double = 0.0

    override fun getNullableDouble(key: String, defaultValue: Double?): Double? = 0.0

    override fun observeDouble(key: String, defaultValue: Double): Flow<Double> = flowOf(0.0)

    override fun setDouble(key: String, value: Double) {
    }

    override fun getFloat(key: String, defaultValue: Float): Float = 0f

    override fun getNullableFloat(key: String, defaultValue: Float?): Float? = 0f

    override fun observeFloat(key: String, defaultValue: Float): Flow<Float> = flowOf(0f)

    override fun setFloat(key: String, value: Float) {
    }

    override fun getString(key: String, defaultValue: String): String = "test"

    override fun getNullableString(key: String, defaultValue: String?): String? = "test"

    override fun observeString(key: String, defaultValue: String): Flow<String> = flowOf("test")

    override fun observeNullableString(key: String, defaultValue: String?): Flow<String?> = flowOf("test")

    override fun setString(key: String, value: String) {
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Set<String> = emptySet()

    override fun observeStringSet(key: String, defaultValue: Set<String>): Flow<Set<String>> = emptyFlow()

    override fun setStringSet(key: String, value: Set<String>) {
    }

    override fun <T : Enum<T>> getEnum(key: String, defaultValue: T, factory: (String) -> T): T = defaultValue

    override fun <T : Enum<T>> getEnumNullable(
        key: String,
        defaultValue: T?,
        factory: (String) -> T
    ): T? = defaultValue

    override fun <T : Enum<T>> observeEnum(
        key: String,
        defaultValue: T,
        factory: (String) -> T
    ): Flow<T> = emptyFlow()

    override fun <T : Enum<T>> observeEnumNullable(
        key: String,
        defaultValue: T?,
        factory: (String) -> T
    ): Flow<T?> = emptyFlow()

    override fun <T : Enum<T>> setEnum(key: String, value: T, factory: (String) -> T) {
    }
}