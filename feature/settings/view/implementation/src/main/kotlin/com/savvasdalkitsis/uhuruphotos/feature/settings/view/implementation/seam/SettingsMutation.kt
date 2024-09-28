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
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSettingState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.MapProviderState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

sealed class SettingsMutation(
    mutation: Mutation<SettingsState>,
) : Mutation<SettingsState> by mutation {

    data class ShowJobStartDialog(val job: JobModel) : SettingsMutation({
        it.copy(showJobStartDialog = job)
    })

    data object HideJobDialog : SettingsMutation({
        it.copy(showJobStartDialog = null)
    })

    data class ShowJobs(val jobStates: List<JobState>) : SettingsMutation({
        it.copy(jobStates = jobStates.toImmutableList())
    })

    data class DisplayLightboxPhotoDiskCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(
            isLoading = false,
            lightboxPhotoDiskCacheState = it.lightboxPhotoDiskCacheState.copy(
                max = limit,
            )
        )
    })

    data class DisplayLightboxPhotoMemCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(
            isLoading = false,
            lightboxPhotoMemCacheState = it.lightboxPhotoMemCacheState.copy(max = limit),
        )
    })

    data class DisplayThumbnailDiskCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(
            isLoading = false,
            thumbnailDiskCacheState = it.thumbnailDiskCacheState.copy(
                max = limit,
            )
        )
    })

    data class DisplayThumbnailMemCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(
            isLoading = false,
            thumbnailMemCacheState = it.thumbnailMemCacheState.copy(max = limit),
        )
    })

    data class DisplayVideoDiskCacheMaxLimit(val limit: Int) : SettingsMutation({
        it.copy(videoDiskCacheState = it.videoDiskCacheState.copy(max = limit))
    })

    data class DisplayLightboxPhotoDiskCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            lightboxPhotoDiskCacheState = it.lightboxPhotoDiskCacheState.copy(
                current = current,
            ),
        )
    })

    data class DisplayLightboxPhotoMemCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            lightboxPhotoMemCacheState = it.lightboxPhotoMemCacheState.copy(current = current),
        )
    })

    data class DisplayThumbnailDiskCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            thumbnailDiskCacheState = it.thumbnailDiskCacheState.copy(
                current = current,
            ),
        )
    })

    data class DisplayThumbnailMemCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(
            isLoading = false,
            thumbnailMemCacheState = it.thumbnailMemCacheState.copy(current = current),
        )
    })

    data class DisplayVideoDiskCacheCurrentUse(val current: Int): SettingsMutation({
        it.copy(videoDiskCacheState = it.videoDiskCacheState.copy(current = current))
    })

    data class DisplayCloudSyncNetworkRequirements(val networkType: NetworkType): SettingsMutation({
        it.copy(cloudSyncNetworkRequirement = networkType)
    })

    data class DisplayCloudSyncRequiresCharging(val requiresCharging: Boolean): SettingsMutation({
        it.copy(cloudSyncRequiresCharging = requiresCharging)
    })

    data class DisplayThemeMode(val themeMode: ThemeMode): SettingsMutation({
        it.copy(themeMode = themeMode)
    })

    data class DisplayFeedMediaItemSyncDisplay(val display: FeedMediaItemSyncDisplayState): SettingsMutation({
        it.copy(feedMediaItemSyncDisplayState = display)
    })

    data class DisplayCollageSpacing(val spacing: Int): SettingsMutation({
        it.copy(collageSpacing = spacing)
    })

    data class DisplayCollageSpacingIncludeEdges(val include: Boolean): SettingsMutation({
        it.copy(collageSpacingIncludeEdges = include)
    })

    data class DisplayCollageShape(val shape: CollageShape): SettingsMutation({
        it.copy(collageShape = shape)
    })

    data class SetRemoteAccess(val hasAccess: Boolean): SettingsMutation({
        it.copy(hasRemoteAccess = hasAccess)
    })

    data class SetUploadsInProgress(val inProgress: Boolean): SettingsMutation({
        it.copy(uploadsInProgress = inProgress)
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

    data class DisplaySendDatabaseEnabled(val enabled: Boolean): SettingsMutation({
        it.copy(isSendDatabaseEnabled = enabled)
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
            lightboxPhotoMemCacheState = it.lightboxPhotoMemCacheState.copy(limit = limit),
            thumbnailMemCacheState = it.thumbnailMemCacheState.copy(limit = limit),
        )
    })

    data class SetDiskCacheUpperLimit(
        val limit: Int,
    ): SettingsMutation({
        it.copy(
            lightboxPhotoDiskCacheState = it.lightboxPhotoDiskCacheState.copy(limit = limit),
            thumbnailDiskCacheState = it.thumbnailDiskCacheState.copy(limit = limit),
            videoDiskCacheState = it.videoDiskCacheState.copy(limit = limit),
        )
    })

    data class DisplayMapProviders(
        val current: MapProvider,
        val available: Set<MapProvider>,
    ) : SettingsMutation({
        it.copy(
            mapProviderState = MapProviderState.SelectedState(
                current, available.toImmutableSet()
            )
        )
    })

    data class DisplayBiometrics(val biometrics: BiometricsSettingState?) : SettingsMutation({
        it.copy(biometrics = biometrics)
    })

    data object DisplayNoMapProvidersOptions : SettingsMutation({
        it.copy(
            mapProviderState = MapProviderState.NoOptionsState
        )
    })

    data class SetFullSyncProgressVisibility(val show: Boolean) : SettingsMutation({
        it.copy(shouldShowFeedSyncProgress = show)
    })

    data class SetFeedDetailsSyncProgressVisibility(val show: Boolean) : SettingsMutation({
        it.copy(shouldShowFeedDetailsSyncProgress = show)
    })

    data class SetPrecacheProgressVisibility(val show: Boolean) : SettingsMutation({
        it.copy(shouldShowPrecacheProgress = show)
    })

    data class SetLocalSyncProgressVisibility(val show: Boolean) : SettingsMutation({
        it.copy(shouldShowLocalSyncProgress = show)
    })

    data class SetAutoHideFeedNavOnScroll(val autoHide: Boolean) : SettingsMutation({
        it.copy(autoHideFeedNavOnScroll = autoHide)
    })
}
