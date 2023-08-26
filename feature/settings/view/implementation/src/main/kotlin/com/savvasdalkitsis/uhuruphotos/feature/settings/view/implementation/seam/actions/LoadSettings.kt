/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.toJobState
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.LIGHTBOX_PHOTO_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.LIGHTBOX_PHOTO_MEMORY
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.THUMBNAIL_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.THUMBNAIL_MEMORY
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.VIDEO_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.AvatarUpdate
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayAnimateVideoThumbnailsEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayBiometrics
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayFeedDaysToRefresh
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayFeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayFeedSyncFrequency
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayFullSyncRequiresCharging
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayLightboxPhotoDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayLightboxPhotoDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayLightboxPhotoMemCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayLightboxPhotoMemCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayLoggingEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayMapProviders
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayMaxAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayNoMapProvidersOptions
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplaySearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplaySendDatabaseEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayShowLibrary
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayShowMemories
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayThemeMode
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayThumbnailDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayThumbnailDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayThumbnailMemCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayThumbnailMemCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayVideoDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayVideoDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetAutoHideFeedNavOnScroll
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetDiskCacheUpperLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetFeedDetailsSyncProgressVisibility
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetFullSyncProgressVisibility
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetLocalSyncProgressVisibility
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetMemoryCacheUpperLimit
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetPrecacheProgressVisibility
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetRemoteAccess
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.SetUploadsInProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.ShowJobs
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting.Enrolled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting.NotEnrolled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data object LoadSettings : SettingsAction() {
    context(SettingsActionsContext) override fun handle(
        state: SettingsState
    ) = with(settingsUseCase) {
        merge(
            welcomeUseCase.observeWelcomeStatus().map { status ->
                SetRemoteAccess(status.hasRemoteAccess)
            },
            observeLightboxPhotoDiskCacheMaxLimit().map(::DisplayLightboxPhotoDiskCacheMaxLimit),
            observeLightboxPhotoMemCacheMaxLimit().map(::DisplayLightboxPhotoMemCacheMaxLimit),
            observeThumbnailDiskCacheMaxLimit().map(::DisplayThumbnailDiskCacheMaxLimit),
            observeThumbnailMemCacheMaxLimit().map(::DisplayThumbnailMemCacheMaxLimit),
            observeVideoDiskCacheMaxLimit().map(::DisplayVideoDiskCacheMaxLimit),
            observeFeedSyncFrequency().map(::DisplayFeedSyncFrequency),
            observeFeedDaysToRefresh().map(::DisplayFeedDaysToRefresh),
            observeFullSyncNetworkRequirements().map(::DisplayFullSyncNetworkRequirements),
            observeFullSyncRequiresCharging().map(::DisplayFullSyncRequiresCharging),
            observeThemeMode().map(::DisplayThemeMode),
            observeSearchSuggestionsEnabledMode().map(::DisplaySearchSuggestionsEnabled),
            observeShareRemoveGpsData().map(::DisplayShareGpsDataEnabled),
            observeShowLibrary().map(::DisplayShowLibrary),
            observeMemoriesEnabled().map(::DisplayShowMemories),
            observeAnimateVideoThumbnails().map(::DisplayAnimateVideoThumbnailsEnabled),
            observeMaxAnimatedVideoThumbnails().map(::DisplayMaxAnimatedVideoThumbnails),
            observeMapProvider().map { current ->
                val available = getAvailableMapProviders()
                when {
                    available.size > 1 -> DisplayMapProviders(current, available)
                    else -> DisplayNoMapProvidersOptions
                }
            },
            observeLoggingEnabled().map(::DisplayLoggingEnabled),
            observeSendDatabaseEnabled().map(::DisplaySendDatabaseEnabled),
            observeFeedMediaItemSyncDisplay().map(::DisplayFeedMediaItemSyncDisplay),
            combine(
                observeBiometricsRequiredForAppAccess(),
                observeBiometricsRequiredForHiddenPhotosAccess(),
                observeBiometricsRequiredForTrashAccess(),
            ) { app, hiddenPhotos, trash ->
                DisplayBiometrics(enrollment(app, hiddenPhotos, trash))
            },
            observeShouldShowFeedSyncProgress().map(::SetFullSyncProgressVisibility),
            observeShouldShowFeedDetailsSyncProgress().map(::SetFeedDetailsSyncProgressVisibility),
            observeShouldShowPrecacheProgress().map(::SetPrecacheProgressVisibility),
            observeShouldShowLocalSyncProgress().map(::SetLocalSyncProgressVisibility),
            observeAutoHideFeedNavOnScroll().map(::SetAutoHideFeedNavOnScroll),
            cacheUseCase.observeCacheCurrentUse(LIGHTBOX_PHOTO_DISK)
                .map(::DisplayLightboxPhotoDiskCacheCurrentUse),
            cacheUseCase.observeCacheCurrentUse(LIGHTBOX_PHOTO_MEMORY)
                .map(::DisplayLightboxPhotoMemCacheCurrentUse),
            cacheUseCase.observeCacheCurrentUse(THUMBNAIL_DISK)
                .map(::DisplayThumbnailDiskCacheCurrentUse),
            cacheUseCase.observeCacheCurrentUse(THUMBNAIL_MEMORY)
                .map(::DisplayThumbnailMemCacheCurrentUse),
            cacheUseCase.observeCacheCurrentUse(VIDEO_DISK)
                .map(::DisplayVideoDiskCacheCurrentUse),
            avatarUseCase.getAvatarState()
                .map(::AvatarUpdate),
            flowOf(systemUseCase.getAvailableSystemMemoryInMb())
                .map(::SetMemoryCacheUpperLimit),
            flowOf(systemUseCase.getAvailableStorageInMb())
                .map(::SetDiskCacheUpperLimit),
            jobsUseCase.observeJobsStatus().map {
                ShowJobs(it.jobs.toJobState)
            },
            uploadsUseCase.observeUploadsInFlight().map { uploads ->
                SetUploadsInProgress(uploads.inProgress)
            }
        )
    }

    context(SettingsActionsContext)
    private fun enrollment(
        app: Boolean,
        hiddenPhotos: Boolean,
        trash: Boolean,
    ) = when (biometricsUseCase.getBiometrics()) {
        Biometrics.Enrolled -> Enrolled(app, hiddenPhotos, trash)
        Biometrics.NotEnrolled -> NotEnrolled
        Biometrics.NoHardware -> null
    }
}
