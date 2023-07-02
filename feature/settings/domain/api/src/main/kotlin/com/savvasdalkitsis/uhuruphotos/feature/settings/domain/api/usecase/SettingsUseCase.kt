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
package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsUseCase {

    fun getLightboxPhotoDiskCacheMaxLimit(): Int
    fun getLightboxPhotoMemCacheMaxLimit(): Int
    fun getThumbnailDiskCacheMaxLimit(): Int
    fun getThumbnailMemCacheMaxLimit(): Int
    fun getVideoDiskCacheMaxLimit(): Int
    fun getFeedSyncFrequency(): Int
    fun getFeedDaysToRefresh(): Int
    fun getFullSyncNetworkRequirements(): NetworkType
    fun getFullSyncRequiresCharging(): Boolean
    fun getShouldPerformPeriodicFullSync(): Boolean
    fun getShareRemoveGpsData(): Boolean
    fun getShowLibrary(): Boolean
    fun getMapProvider(): MapProvider
    fun getAvailableMapProviders(): Set<MapProvider>
    fun getLoggingEnabled(): Boolean
    fun getBiometricsRequiredForAppAccess(): Boolean
    fun getBiometricsRequiredForHiddenPhotosAccess(): Boolean
    fun getBiometricsRequiredForTrashAccess(): Boolean
    fun getMemoriesEnabled(): Boolean
    fun getAnimateVideoThumbnails(): Boolean
    fun getMaxAnimatedVideoThumbnails(): Int
    fun getShowBannerAskingForLocalMediaPermissionsOnFeed(): Boolean
    fun getShowBannerAskingForLocalMediaPermissionsOnHeatmap(): Boolean
    fun getFeedMediaItemSyncDisplay(): FeedMediaItemSyncDisplay
    fun getShouldShowFeedSyncProgress(): Boolean
    fun getShouldShowPrecacheProgress(): Boolean
    fun getShouldShowLocalSyncProgress(): Boolean

    fun observeLightboxPhotoDiskCacheMaxLimit(): Flow<Int>
    fun observeLightboxPhotoMemCacheMaxLimit(): Flow<Int>
    fun observeThumbnailDiskCacheMaxLimit(): Flow<Int>
    fun observeThumbnailMemCacheMaxLimit(): Flow<Int>
    fun observeVideoDiskCacheMaxLimit(): Flow<Int>
    fun observeFeedSyncFrequency(): Flow<Int>
    fun observeFeedDaysToRefresh(): Flow<Int>
    fun observeFullSyncNetworkRequirements(): Flow<NetworkType>
    fun observeFullSyncRequiresCharging(): Flow<Boolean>
    fun observeThemeMode(): Flow<ThemeMode>
    fun observeSearchSuggestionsEnabledMode(): Flow<Boolean>
    fun observeShareRemoveGpsData(): Flow<Boolean>
    fun observeShowLibrary(): Flow<Boolean>
    fun observeThemeModeState(): StateFlow<ThemeMode>
    fun observeMapProvider(): Flow<MapProvider>
    fun observeLoggingEnabled(): Flow<Boolean>
    fun observeBiometricsRequiredForAppAccess(): Flow<Boolean>
    fun observeBiometricsRequiredForHiddenPhotosAccess(): Flow<Boolean>
    fun observeBiometricsRequiredForTrashAccess(): Flow<Boolean>
    fun observeMemoriesEnabled(): Flow<Boolean>
    fun observeAnimateVideoThumbnails(): Flow<Boolean>
    fun observeMaxAnimatedVideoThumbnails(): Flow<Int>
    fun observeFeedMediaItemSyncDisplay(): Flow<FeedMediaItemSyncDisplay>
    fun observeShouldShowFeedSyncProgress(): Flow<Boolean>
    fun observeShouldShowPrecacheProgress(): Flow<Boolean>
    fun observeShouldShowLocalSyncProgress(): Flow<Boolean>

    fun setLightboxPhotoDiskCacheMaxLimit(sizeInMb: Int)
    fun setLightboxPhotoMemCacheMaxLimit(sizeInMb: Int)
    fun setThumbnailDiskCacheMaxLimit(sizeInMb: Int)
    fun setThumbnailMemCacheMaxLimit(sizeInMb: Int)
    fun setVideoDiskCacheMaxLimit(sizeInMb: Int)
    fun setFeedSyncFrequency(frequency: Int)
    fun setFeedFeedDaysToRefresh(days: Int)
    fun setFullSyncNetworkRequirements(networkType: NetworkType)
    fun setFullSyncRequiresCharging(requiresCharging: Boolean)
    fun setShouldPerformPeriodicFullSync(perform: Boolean)
    fun setThemeMode(mode: ThemeMode)
    fun setSearchSuggestionsEnabled(enabled: Boolean)
    fun setShareRemoveGpsData(enabled: Boolean)
    fun setShowLibrary(show: Boolean)
    fun setMapProvider(provider: MapProvider)
    fun setLoggingEnabled(enabled: Boolean)
    fun setBiometricsRequiredForAppAccess(required: Boolean)
    fun setBiometricsRequiredForHiddenPhotosAccess(required: Boolean)
    fun setBiometricsRequiredForTrashAccess(required: Boolean)
    fun setMemoriesEnabled(enabled: Boolean)
    fun setAnimateVideoThumbnails(animate: Boolean)
    fun setMaxAnimatedVideoThumbnails(max: Int)
    fun setShowBannerAskingForLocalMediaPermissionsOnFeed(show: Boolean)
    fun setShowBannerAskingForLocalMediaPermissionsOnHeatmap(show: Boolean)
    fun setFeedMediaItemSyncDisplay(display: FeedMediaItemSyncDisplay)
    fun setShouldShowFeedSyncProgress(show: Boolean)
    fun setShouldShowPrecacheProgress(show: Boolean)
    fun setShouldShowLocalSyncProgress(show: Boolean)
}