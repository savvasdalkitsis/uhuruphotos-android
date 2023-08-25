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

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode

data class SettingsState(
    val isLoading: Boolean = true,
    val lightboxPhotoDiskCache: Cache = Cache(cacheType = CacheType.LIGHTBOX_PHOTO_DISK),
    val lightboxPhotoMemCache: Cache = Cache(cacheType = CacheType.LIGHTBOX_PHOTO_MEMORY),
    val thumbnailDiskCache: Cache = Cache(cacheType = CacheType.THUMBNAIL_DISK),
    val thumbnailMemCache: Cache = Cache(cacheType = CacheType.THUMBNAIL_MEMORY),
    val videoDiskCache: Cache = Cache(cacheType = CacheType.VIDEO_DISK),
    val feedDaysToRefresh: Int = 0,
    val feedSyncFrequency: Int? = null,
    val avatarState: AvatarState = AvatarState(),
    val jobStates: List<JobState> = emptyList(),
    val showJobStartDialog: Job? = null,
    val fullSyncNetworkRequirement: NetworkType? = null,
    val fullSyncRequiresCharging: Boolean = false,
    val uploadsInProgress: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.default,
    val searchSuggestionsEnabled: Boolean = true,
    val shareRemoveGpsDataEnabled: Boolean = false,
    val showLibrary: Boolean = true,
    val animateVideoThumbnails: Boolean = true,
    val maxAnimatedVideoThumbnails: Int = 3,
    val mapProviderState: MapProviderState = MapProviderState.NoOptions,
    val isLoggingEnabled: Boolean = false,
    val biometrics: BiometricsSetting? = null,
    val showMemories: Boolean = true,
    val autoHideFeedNavOnScroll: Boolean = true,
    val feedMediaItemSyncDisplay: FeedMediaItemSyncDisplay = FeedMediaItemSyncDisplay.default,
    val shouldShowFeedSyncProgress: Boolean = false,
    val shouldShowFeedDetailsSyncProgress: Boolean = false,
    val shouldShowPrecacheProgress: Boolean = false,
    val shouldShowLocalSyncProgress: Boolean = false,
    val hasRemoteAccess: Boolean = false,
)