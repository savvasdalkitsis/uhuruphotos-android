package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.*
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
    }
}