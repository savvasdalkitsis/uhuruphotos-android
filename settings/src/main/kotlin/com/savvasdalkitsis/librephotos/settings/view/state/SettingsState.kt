package com.savvasdalkitsis.librephotos.settings.view.state

import androidx.work.NetworkType
import com.savvasdalkitsis.librephotos.userbadge.api.view.state.UserInformationState

data class SettingsState(
    val isLoading: Boolean = true,
    val diskCacheMax: Int = 0,
    val diskCacheCurrent: Int = 0,
    val memCacheMax: Int = 0,
    val memCacheCurrent: Int = 0,
    val feedSyncFrequency: Int? = null,
    val userInformationState: UserInformationState = UserInformationState(),
    val showFullFeedSyncDialog: Boolean = false,
    val fullSyncButtonEnabled: Boolean = false,
    val fullSyncNetworkRequirement: NetworkType? = null,
    val fullSyncRequiresCharging: Boolean = false,
)