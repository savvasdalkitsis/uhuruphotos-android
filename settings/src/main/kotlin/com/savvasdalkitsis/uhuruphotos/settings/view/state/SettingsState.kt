package com.savvasdalkitsis.uhuruphotos.settings.view.state

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

data class SettingsState(
    val isLoading: Boolean = true,
    val imageDiskCacheMax: Int = 0,
    val imageDiskCacheCurrent: Int = 0,
    val videoDiskCacheMax: Int = 0,
    val videoDiskCacheCurrent: Int = 0,
    val imageMemCacheMax: Int = 0,
    val imageMemCacheCurrent: Int = 0,
    val feedSyncFrequency: Int? = null,
    val userInformationState: UserInformationState = UserInformationState(),
    val showFullFeedSyncDialog: Boolean = false,
    val fullSyncButtonEnabled: Boolean = false,
    val fullSyncNetworkRequirement: NetworkType? = null,
    val fullSyncRequiresCharging: Boolean = false,
)