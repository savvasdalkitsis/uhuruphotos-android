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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.state.ThemeSettingsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SettingsState(
    val isLoading: Boolean = true,
    val lightboxPhotoDiskCacheState: CacheState = CacheState(cacheType = CacheType.LIGHTBOX_PHOTO_DISK),
    val lightboxPhotoMemCacheState: CacheState = CacheState(cacheType = CacheType.LIGHTBOX_PHOTO_MEMORY),
    val thumbnailDiskCacheState: CacheState = CacheState(cacheType = CacheType.THUMBNAIL_DISK),
    val thumbnailMemCacheState: CacheState = CacheState(cacheType = CacheType.THUMBNAIL_MEMORY),
    val videoDiskCacheState: CacheState = CacheState(cacheType = CacheType.VIDEO_DISK),
    val avatarState: AvatarState = AvatarState(),
    val jobStates: ImmutableList<JobState> = persistentListOf(),
    val showJobStartDialog: JobModel? = null,
    val cloudSyncNetworkRequirement: NetworkType? = null,
    val cloudSyncRequiresCharging: Boolean = false,
    val uploadsInProgress: Boolean = false,
    val themeSettingsState: ThemeSettingsState = ThemeSettingsState(),
    val searchSuggestionsEnabled: Boolean = true,
    val shareRemoveGpsDataEnabled: Boolean = false,
    val showLibrary: Boolean = true,
    val animateVideoThumbnails: Boolean = true,
    val maxAnimatedVideoThumbnails: Int = 3,
    val mapProviderState: MapProviderState = MapProviderState.NoOptionsState,
    val isLoggingEnabled: Boolean = false,
    val isSendDatabaseEnabled: Boolean = false,
    val biometrics: BiometricsSettingState? = null,
    val showMemories: Boolean = true,
    val memoriesParallax: Boolean = true,
    val autoHideFeedNavOnScroll: Boolean = true,
    val feedMediaItemSyncDisplayState: FeedMediaItemSyncDisplayState = FeedMediaItemSyncDisplayState.default,
    val shouldShowFeedSyncProgress: Boolean = false,
    val shouldShowFeedDetailsSyncProgress: Boolean = false,
    val shouldShowPrecacheProgress: Boolean = false,
    val shouldShowLocalSyncProgress: Boolean = false,
    val hasRemoteAccess: Boolean = false,
    val uploadCapability: UploadCapability = UploadCapability.NotSetUpWithAServer,
)