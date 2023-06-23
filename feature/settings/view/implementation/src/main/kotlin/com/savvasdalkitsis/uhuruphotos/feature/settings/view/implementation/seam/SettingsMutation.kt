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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.MapProviderState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode

sealed class SettingsMutation(
    mutation: Mutation<SettingsState>,
) : Mutation<SettingsState> by mutation {

    data class ShowJobStartDialog(val job: Job) : SettingsMutation({
        it.copy(showJobStartDialog = job)
    })

    data object HideJobDialog : SettingsMutation({
        it.copy(showJobStartDialog = null)
    })

    data class ShowJobs(val jobStates: List<JobState>) : SettingsMutation({
        it.copy(jobStates = jobStates)
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

    data class DisplayFeedMediaItemSyncDisplay(val display: FeedMediaItemSyncDisplay): SettingsMutation({
        it.copy(feedMediaItemSyncDisplay = display)
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

    data class DisplayShowMemories(val show: Boolean): SettingsMutation({
        it.copy(showMemories = show)
    })

    data class DisplayLoggingEnabled(val enabled: Boolean): SettingsMutation({
        it.copy(isLoggingEnabled = enabled)
    })

    data class DisplayAnimateVideoThumbnailsEnabled(val enabled: Boolean): SettingsMutation({
        it.copy(animateVideoThumbnails = enabled)
    })

    data class DisplayMaxAnimatedVideoThumbnails(val max: Int): SettingsMutation({
        it.copy(maxAnimatedVideoThumbnails = max)
    })

    data class AvatarUpdate(
        val avatarState: AvatarState,
    ): SettingsMutation({
        it.copy(
            isLoading = false,
            avatarState = avatarState,
        )
    })

    data class SetMemoryCacheUpperLimit(
        val limit: Int,
    ): SettingsMutation({
        it.copy(
            imageMemCacheLimit = limit,
        )
    })

    data class SetDiskCacheUpperLimit(
        val limit: Int,
    ): SettingsMutation({
        it.copy(
            imageDiskCacheLimit = limit,
            videoDiskCacheLimit = limit,
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

    data object DisplayNoMapProvidersOptions : SettingsMutation({
        it.copy(
            mapProviderState = MapProviderState.NoOptions
        )
    })

    data class SetFullSyncProgressVisibility(val show: Boolean) : SettingsMutation({
        it.copy(shouldShowFeedSyncProgress = show)
    })

    data class SetPrecacheProgressVisibility(val show: Boolean) : SettingsMutation({
        it.copy(shouldShowPrecacheProgress = show)
    })
}
