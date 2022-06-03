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
package com.savvasdalkitsis.uhuruphotos.settings.view.state

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

internal data class SettingsState(
    val isLoading: Boolean = true,
    val imageDiskCacheMax: Int = 0,
    val imageDiskCacheCurrent: Int = 0,
    val videoDiskCacheMax: Int = 0,
    val videoDiskCacheCurrent: Int = 0,
    val imageMemCacheMax: Int = 0,
    val imageMemCacheCurrent: Int = 0,
    val feedSyncFrequency: Int? = null,
    val userInformationState: UserInformationState = UserInformationState(),
    val showFullFeedSyncDialog: Boolean = false,
    val fullSyncButtonEnabled: Boolean = false,
    val fullSyncNetworkRequirement: NetworkType? = null,
    val fullSyncRequiresCharging: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.default,
    val fullSyncJobProgress: Int = 0,
    val searchSuggestionsEnabled: Boolean = true,
    val shareRemoveGpsDataEnabled: Boolean = false,
    val showLibrary: Boolean = true,
)