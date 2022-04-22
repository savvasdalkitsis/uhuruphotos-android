package com.savvasdalkitsis.librephotos.settings.viewmodel

sealed class SettingsMutation {
    data class DisplayDiskCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayMemCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayDiskCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayMemCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayFeedSyncFrequency(val frequency: Int): SettingsMutation()
}
