package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode

sealed class SettingsAction {

    data class ChangeImageDiskCache(val sizeInMb: Float) : SettingsAction()
    data class ChangeVideoDiskCache(val sizeInMb: Float) : SettingsAction()
    data class ChangeImageMemCache(val sizeInMb: Float) : SettingsAction()
    data class ChangeFullSyncNetworkRequirements(val networkType: NetworkType) : SettingsAction()
    data class ChangeFullSyncChargingRequirements(val requiredCharging: Boolean) : SettingsAction()
    data class ChangeSearchSuggestionsEnabled(val enabled: Boolean) : SettingsAction()

    data class FeedSyncFrequencyChanged(val frequency: Float, val upperLimit: Float) : SettingsAction()
    data class ChangeThemeMode(val themeMode: ThemeMode) : SettingsAction()

    object LoadSettings : SettingsAction()
    object NavigateBack : SettingsAction()
    object ClearImageDiskCache : SettingsAction()
    object ClearImageMemCache : SettingsAction()
    object ClearVideoDiskCache : SettingsAction()
    object AskForFullFeedSync : SettingsAction()
    object DismissFullFeedSyncDialog : SettingsAction()
    object PerformFullFeedSync : SettingsAction()
}
