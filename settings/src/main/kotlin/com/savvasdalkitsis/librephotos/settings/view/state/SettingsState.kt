package com.savvasdalkitsis.librephotos.settings.view.state

data class SettingsState(
    val isLoading: Boolean = true,
    val diskCacheMax: Int = 0,
    val diskCacheCurrent: Int = 0,
    val memCacheMax: Int = 0,
    val memCacheCurrent: Int = 0,
    val feedSyncFrequency: Int? = null,
)