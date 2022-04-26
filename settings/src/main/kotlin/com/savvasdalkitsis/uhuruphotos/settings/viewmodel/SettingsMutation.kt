package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

sealed class SettingsMutation {
    object ShowFullFeedSyncDialog : SettingsMutation()
    object HideFullFeedSyncDialog : SettingsMutation()
    object EnableFullSyncButton : SettingsMutation()
    object DisableFullSyncButton : SettingsMutation()

    data class DisplayDiskCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayMemCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayVideoDiskCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayImageDiskCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayImageMemCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayVideoDiskCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayFeedSyncFrequency(val frequency: Int): SettingsMutation()
    data class DisplayFullSyncNetworkRequirements(val networkType: NetworkType): SettingsMutation()
    data class DisplayFullSyncRequiresCharging(val requiresCharging: Boolean): SettingsMutation()
    data class DisplayThemeMode(val themeMode: ThemeMode): SettingsMutation()
    data class UserBadgeUpdate(val userInformationState: UserInformationState): SettingsMutation()
}
