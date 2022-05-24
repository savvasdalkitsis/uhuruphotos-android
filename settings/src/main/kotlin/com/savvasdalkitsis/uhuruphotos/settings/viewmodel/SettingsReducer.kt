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
package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFeedSyncFrequency
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFullSyncProgress
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFullSyncRequiresCharging
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayImageDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayImageMemCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayMemCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplaySearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayThemeMode
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayVideoDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayVideoDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.EnableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.HideFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.ShowFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.UserBadgeUpdate
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun settingsReducer(): Reducer<SettingsState, SettingsMutation> = { state, mutation ->
    when (mutation) {
        is DisplayDiskCacheMaxLimit -> state.copy(
            isLoading = false,
            imageDiskCacheMax = mutation.limit,
        )
        is DisplayImageDiskCacheCurrentUse -> state.copy(
            isLoading = false,
            imageDiskCacheCurrent = mutation.current,
        )
        is DisplayMemCacheMaxLimit -> state.copy(
            isLoading = false,
            imageMemCacheMax = mutation.limit,
        )
        is DisplayImageMemCacheCurrentUse -> state.copy(
            isLoading = false,
            imageMemCacheCurrent = mutation.current,
        )
        is DisplayFeedSyncFrequency -> state.copy(
            isLoading = false,
            feedSyncFrequency = mutation.frequency,
        )
        is UserBadgeUpdate -> state.copy(
            isLoading = false,
            userInformationState = mutation.userInformationState,
        )
        HideFullFeedSyncDialog -> state.copy(showFullFeedSyncDialog = false)
        ShowFullFeedSyncDialog -> state.copy(showFullFeedSyncDialog = true)
        DisableFullSyncButton -> state.copy(fullSyncButtonEnabled = false)
        EnableFullSyncButton -> state.copy(fullSyncButtonEnabled = true)
        is DisplayFullSyncNetworkRequirements -> state.copy(fullSyncNetworkRequirement = mutation.networkType)
        is DisplayFullSyncRequiresCharging -> state.copy(fullSyncRequiresCharging = mutation.requiresCharging)
        is DisplayVideoDiskCacheCurrentUse -> state.copy(videoDiskCacheCurrent = mutation.current)
        is DisplayVideoDiskCacheMaxLimit -> state.copy(videoDiskCacheMax = mutation.limit)
        is DisplayThemeMode -> state.copy(themeMode = mutation.themeMode)
        is DisplayFullSyncProgress -> state.copy(fullSyncJobProgress = mutation.progress)
        is DisplaySearchSuggestionsEnabled -> state.copy(searchSuggestionsEnabled = mutation.enabled)
        is DisplayShareGpsDataEnabled -> state.copy(shareRemoveGpsDataEnabled = mutation.enabled)
    }
}