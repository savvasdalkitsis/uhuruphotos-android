package com.savvasdalkitsis.librephotos.settings.viewmodel

import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer

fun settingsReducer(): Reducer<SettingsState, SettingsMutation> = { state, mutation ->
    when (mutation) {
        is SettingsMutation.DisplayDiskCacheMaxLimit -> state.copy(
            isLoading = false,
            diskCacheMax = mutation.limit,
        )
        is SettingsMutation.DisplayDiskCacheCurrentUse -> state.copy(
            isLoading = false,
            diskCacheCurrent = mutation.current,
        )
        is SettingsMutation.DisplayMemCacheMaxLimit -> state.copy(
            isLoading = false,
            memCacheMax = mutation.limit,
        )
        is SettingsMutation.DisplayMemCacheCurrentUse -> state.copy(
            isLoading = false,
            memCacheCurrent = mutation.current,
        )
        is SettingsMutation.DisplayFeedSyncFrequency -> state.copy(
            isLoading = false,
            feedSyncFrequency = mutation.frequency,
        )
        is SettingsMutation.UserBadgeUpdate -> state.copy(
            isLoading = false,
            userInformationState = mutation.userInformationState,
        )
        SettingsMutation.HideFullFeedSyncDialog -> state.copy(showFullFeedSyncDialog = false)
        SettingsMutation.ShowFullFeedSyncDialog -> state.copy(showFullFeedSyncDialog = true)
        SettingsMutation.DisableFullSyncButton -> state.copy(fullSyncButtonEnabled = false)
        SettingsMutation.EnableFullSyncButton -> state.copy(fullSyncButtonEnabled = true)
    }
}