package com.savvasdalkitsis.librephotos.settings.viewmodel

import androidx.work.NetworkType

sealed class SettingsAction {

    data class ChangeDiskCache(val sizeInMb: Float) : SettingsAction()
    data class ChangeMemCache(val sizeInMb: Float) : SettingsAction()
    data class ChangeFullSyncNetworkRequirements(val networkType: NetworkType) : SettingsAction()
    data class ChangeFullSyncChargingRequirements(val requiredCharging: Boolean) : SettingsAction()

    data class FeedSyncFrequencyChanged(val frequency: Float, val upperLimit: Float) : SettingsAction()

    object LoadSettings : SettingsAction()
    object NavigateBack : SettingsAction()
    object ClearDiskCache : SettingsAction()
    object ClearMemCache : SettingsAction()
    object AskForFullFeedSync : SettingsAction()
    object DismissFullFeedSyncDialog : SettingsAction()
    object PerformFullFeedSync : SettingsAction()
}
