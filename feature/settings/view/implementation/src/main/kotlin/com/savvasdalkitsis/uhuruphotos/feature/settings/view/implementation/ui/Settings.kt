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
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.UhuruAvatar
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.JobPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.AboutPressed
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeThemeMode
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeThemeVariant
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.CollageShapeChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.CollageSpacingChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.CollageSpacingEdgeChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.DismissJobDialog
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.StartJob
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.controller.SettingsViewStateController
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.MapProviderState.SelectedState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.staggered.UhuruStaggeredGrid
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.UhuruCollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.UhuruSuperGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.UhuruTextRow
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.UhuruThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun Settings(
    controller: SettingsViewStateController,
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    UhuruScaffold(
        title = { Text(stringResource(string.settings)) },
        actionBarContent = {
            UhuruActionIcon(
                onClick = { controller.collapseAll() },
                icon = drawable.ic_expand_all,
                contentDescription = stringResource(string.expand_all),
            )
            UhuruActionIcon(
                onClick = { controller.expandAll() },
                icon = drawable.ic_collapse_all,
                contentDescription = stringResource(string.collapse_all),
            )
            UhuruAvatar(state = state.avatarState)
        },
        navigationIcon = { UhuruUpNavButton() }
    ) { contentPadding ->
        if (state.isLoading) {
            UhuruFullLoading()
        } else {
            val columns = when (LocalWindowSize.current.widthSizeClass) {
                Compact -> 1
                Medium -> 2
                else -> 3
            }
            UhuruStaggeredGrid(
                modifier = Modifier.padding(horizontal = 8.dp),
                contentPadding = contentPadding,
                syncScrolling = false,
                itemSpacing = 8.dp,
                columnCount = columns,
            ) {
                UhuruSuperGroup(controller.ui) {
                    UhuruCollapsibleGroup(groupState = controller.uiFeed) {
                        SettingsFeedUI(state, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.uiTheme) {
                        UhuruThemeSettings(
                            state = state.themeSettingsState,
                            onCollageSpacingChanged = { action(CollageSpacingChanged(it)) },
                            onCollageSpacingEdgeChanged = { action(CollageSpacingEdgeChanged(it)) },
                            onChangeThemeVariant = { theme, contrast -> action(ChangeThemeVariant(theme, contrast)) },
                            onChangeThemeMode = { action(ChangeThemeMode(it)) },
                            onCollageShapeChanged = { action(CollageShapeChanged(it)) }
                        )
                    }
                    UhuruCollapsibleGroup(groupState = controller.uiProgress) {
                        SettingsProgressUI(state, action)
                    }
                    if (state.hasRemoteAccess) {
                        UhuruCollapsibleGroup(groupState = controller.uiSearch) {
                            SettingsSearch(state, action)
                        }
                    }
                    UhuruCollapsibleGroup(groupState = controller.uiLibrary) {
                        SettingsLibrary(state, action)
                    }
//                    Group(controller.uiVideo) {
//                        SettingsVideo(state, action)
//                    }
                    val mapProvider = state.mapProviderState
                    if (mapProvider is SelectedState) {
                        UhuruCollapsibleGroup(groupState = controller.uiMaps) {
                            SettingsMaps(mapProvider, action)
                        }
                    }
                }
                UhuruSuperGroup(controller.privacy) {
                    state.biometrics?.let {
                        UhuruCollapsibleGroup(groupState = controller.privacyBiometrics) {
                            SettingsBiometrics(it, action)
                        }
                    }
                    UhuruCollapsibleGroup(groupState = controller.privacyShare) {
                        SettingsShare(state, action)
                    }
                }
                UhuruSuperGroup(controller.jobs) {
                    UhuruCollapsibleGroup(groupState = controller.jobsStatus) {
                        SettingsJobsStatus(state, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.jobsCloudSync) {
                        UhuruTextRow(stringResource(string.upload_capability))
                        UhuruTextRow(state.uploadCapability.name)
                        if (state.hasRemoteAccess) {
                            SettingsJobsCloudSyncConfiguration(state, action)
                        }
                    }
                }
                UhuruSuperGroup(controller.advanced) {
                    UhuruCollapsibleGroup(groupState = controller.advancedLightboxPhotoDiskCache) {
                        SettingsCache(state.lightboxPhotoDiskCacheState, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.advancedLightboxPhotoMemoryCache) {
                        SettingsCache(state.lightboxPhotoMemCacheState, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.advancedThumbnailDiskCache) {
                        SettingsCache(state.thumbnailDiskCacheState, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.advancedThumbnailMemoryCache) {
                        SettingsCache(state.thumbnailMemCacheState, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.advancedVideoDiskCache) {
                        SettingsCache(state.videoDiskCacheState, action)
                    }
                    UhuruCollapsibleGroup(groupState = controller.advancedLocalMedia) {
                        SettingsLocalMedia(action)
                    }
                }
                UhuruSuperGroup(controller.help) {
                    UhuruCollapsibleGroup(groupState = controller.helpFeedback) {
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
private fun SettingsPreview() {
    PreviewAppTheme {
        Settings(
            controller = SettingsViewStateController(preferences()),
            state = SettingsState(isLoading = false, hasRemoteAccess = true)
        ) {}
    }
}

@Composable
private fun preferences() = object : Preferences {
    override fun remove(key: String) {}
    override fun contains(key: String): Boolean = true

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = false

    override fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> = flowOf(false)

    override fun setBoolean(key: String, value: Boolean) {
    }

    override fun getInt(key: String, defaultValue: Int): Int = 0

    override fun getNullableInt(key: String, defaultValue: Int?): Int = 0

    override fun observeInt(key: String, defaultValue: Int): Flow<Int> = flowOf(0)

    override fun setInt(key: String, value: Int) {
    }

    override fun getDouble(key: String, defaultValue: Double): Double = 0.0

    override fun getNullableDouble(key: String, defaultValue: Double?): Double = 0.0

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