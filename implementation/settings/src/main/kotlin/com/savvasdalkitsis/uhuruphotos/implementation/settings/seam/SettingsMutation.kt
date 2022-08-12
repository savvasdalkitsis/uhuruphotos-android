/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.settings.seam

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.UserInformationState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.BiometricsSetting
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.MapProviderState
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState

internal sealed class SettingsMutation(
    mutation: Mutation<SettingsState>,
) : Mutation<SettingsState> by mutation {

    object ShowFullFeedSyncDialog : SettingsMutation({
        it.copy(showFullFeedSyncDialog = true)
    })

    object HideFullFeedSyncDialog : SettingsMutation({
        it.copy(showFullFeedSyncDialog = false)
    })

    object EnableFullSyncButton : SettingsMutation({
        it.copy(fullSyncButtonEnabled = true)
    })

    object DisableFullSyncButton : SettingsMutation({
        it.copy(fullSyncButtonEnabled = false)
    })

    data class DisplayFullSyncProgress(val progress: Int) : SettingsMutation({
        it.copy(fullSyncJobProgress = progress)
    })

    data class DisplayDiskCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(
            isLoading = false,
            imageDiskCacheMax = limit,
        )
    })

    data class DisplayMemCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(
            isLoading = false,
            imageMemCacheMax = limit,
        )
    })

    data class DisplayVideoDiskCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(videoDiskCacheMax = limit)
    })

    data class DisplayImageDiskCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            imageDiskCacheCurrent = current,
        )
    })

    data class DisplayImageMemCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            imageMemCacheCurrent = current,
        )
    })

    data class DisplayVideoDiskCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(videoDiskCacheCurrent = current)
    })

    data class DisplayFeedSyncFrequency(val frequency: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            feedSyncFrequency = frequency,
        )
    })

    data class DisplayFeedDaystoRefresh(val days: Int): SettingsMutation({
        it.copy(
            feedDaysToRefresh = days,
        )
    })

    data class DisplayFullSyncNetworkRequirements(val networkType: NetworkType): SettingsMutation({
        it.copy(fullSyncNetworkRequirement = networkType)
    })

    data class DisplayFullSyncRequiresCharging(val requiresCharging: Boolean): SettingsMutation({
        it.copy(fullSyncRequiresCharging = requiresCharging)
    })

    data class DisplayThemeMode(val themeMode: ThemeMode): SettingsMutation({
        it.copy(themeMode = themeMode)
    })

    data class DisplaySearchSuggestionsEnabled(val enabled: Boolean): SettingsMutation({
        it.copy(searchSuggestionsEnabled = enabled)
    })

    data class DisplayShareGpsDataEnabled(val enabled: Boolean): SettingsMutation({
        it.copy(shareRemoveGpsDataEnabled = enabled)
    })

    data class DisplayShowLibrary(val show: Boolean): SettingsMutation({
        it.copy(showLibrary = show)
    })

    data class DisplayLoggingEnabled(val enabled: Boolean): SettingsMutation({
        it.copy(isLoggingEnabled = enabled)
    })

    data class UserBadgeUpdate(
        val userInformationState: UserInformationState,
    ): SettingsMutation({
        it.copy(
            isLoading = false,
            userInformationState = userInformationState,
        )
    })

    data class DisplayMapProviders(
        val current: MapProvider,
        val available: Set<MapProvider>,
    ) : SettingsMutation({
        it.copy(
            mapProviderState = MapProviderState.Selected(
                current, available
            )
        )
    })

    data class DisplayBiometrics(val biometrics: BiometricsSetting?) : SettingsMutation({
        it.copy(biometrics = biometrics)
    })

    object DisplayNoMapProvidersOptions : SettingsMutation({
        it.copy(
            mapProviderState = MapProviderState.NoOptions
        )
    })
}
