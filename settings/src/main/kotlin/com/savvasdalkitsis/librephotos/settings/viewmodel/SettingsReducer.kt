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
    }
}