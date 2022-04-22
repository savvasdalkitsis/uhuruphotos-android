package com.savvasdalkitsis.librephotos.settings.viewmodel

sealed class SettingsAction {

    data class ChangeDiskCache(val sizeInMb: Float) : SettingsAction()
    data class ChangeMemCache(val sizeInMb: Float) : SettingsAction()
    object LoadSettings : SettingsAction()
    object NavigateBack : SettingsAction()
    object ClearDiskCache : SettingsAction()
    object ClearMemCache : SettingsAction()
}
