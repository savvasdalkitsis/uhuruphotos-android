package com.savvasdalkitsis.librephotos.settings.viewmodel

import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsMutation.*
import com.savvasdalkitsis.librephotos.viewmodel.Reducer

fun settingsReducer(): Reducer<SettingsState, SettingsMutation> = { state, mutation ->
    when (mutation) {
        is DisplayDiskCacheMaxLimit -> state.copy(
            isLoading = false,
            diskCacheMax = mutation.limit,
        )
        is DisplayDiskCacheCurrentUse -> state.copy(
            isLoading = false,
            diskCacheCurrent = mutation.current,
        )
        is DisplayMemCacheMaxLimit -> state.copy(
            isLoading = false,
            memCacheMax = mutation.limit,
        )
        is DisplayMemCacheCurrentUse -> state.copy(
            isLoading = false,
            memCacheCurrent = mutation.current,
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
    }
}