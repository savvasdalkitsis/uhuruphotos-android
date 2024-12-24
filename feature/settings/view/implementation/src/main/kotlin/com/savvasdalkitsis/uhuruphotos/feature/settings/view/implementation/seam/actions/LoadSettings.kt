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
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSettingState.EnrolledState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSettingState.NotEnrolledState
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data object LoadSettings : SettingsAction() {
    override fun SettingsActionsContext.handle(
        state: SettingsState
    ) = with(settingsUseCase) {
        with(settingsUIUseCase) {
        merge(
            welcomeUseCase.observeWelcomeStatus().map { status ->
                SetRemoteAccess(status.hasRemoteAccess)
            },
            observeLightboxPhotoDiskCacheMaxLimit().map(::DisplayLightboxPhotoDiskCacheMaxLimit),
            observeLightboxPhotoMemCacheMaxLimit().map(::DisplayLightboxPhotoMemCacheMaxLimit),
            observeThumbnailDiskCacheMaxLimit().map(::DisplayThumbnailDiskCacheMaxLimit),
            observeThumbnailMemCacheMaxLimit().map(::DisplayThumbnailMemCacheMaxLimit),
            observeVideoDiskCacheMaxLimit().map(::DisplayVideoDiskCacheMaxLimit),
            observeCloudSyncNetworkRequirements().map(::DisplayCloudSyncNetworkRequirements),
            observeCloudSyncRequiresCharging().map(::DisplayCloudSyncRequiresCharging),
            observeThemeMode().map(::DisplayThemeMode),
            observeThemeVariant().map(::DisplayThemeVariant),
            observeThemeContrast().map(::DisplayThemeContrast),
            observeSearchSuggestionsEnabledMode().map(::DisplaySearchSuggestionsEnabled),
            observeShareRemoveGpsData().map(::DisplayShareGpsDataEnabled),
            observeShowLibrary().map(::DisplayShowLibrary),
            observeMemoriesEnabled().map(::DisplayShowMemories),
            observeMemoriesParallaxEnabled().map(::EnableMemoriesParallax),
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
            observeCollageSpacing().map(::DisplayCollageSpacing),
            observeCollageSpacingIncludeEdges().map(::DisplayCollageSpacingIncludeEdges),
            observeCollageShape().map(::DisplayCollageShape),
            combine(
                observeBiometricsRequiredForAppAccess(),
                observeBiometricsRequiredForHiddenPhotosAccess(),
                observeBiometricsRequiredForTrashAccess(),
                biometricsUseCase.observeBiometrics(),
            ) { app, hiddenPhotos, trash, biometrics ->
                DisplayBiometrics(biometrics.enrollment(app, hiddenPhotos, trash))
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
            },
            flow<UploadCapability> { uploadUseCase.canUpload() }
                .map(::SetUploadCapability),
        )
    }}

    private fun Biometrics.enrollment(
        app: Boolean,
        hiddenPhotos: Boolean,
        trash: Boolean,
    ) = when (this) {
        Biometrics.Enrolled -> EnrolledState(app, hiddenPhotos, trash)
        Biometrics.NotEnrolled -> NotEnrolledState
        Biometrics.NoHardware -> null
    }
}
