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
package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.implementation.usecase

import android.content.Context
import androidx.work.NetworkType
import com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING
import com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
import com.google.android.gms.common.ConnectionResult.SIGN_IN_REQUIRED
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.ALWAYS_OFF
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.minCacheSize
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.MapBox
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.future.asCompletableFuture
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class SettingsUseCase @Inject constructor(
    private val preferences: Preferences,
    private val welcomeUseCase: WelcomeUseCase,
    @ApplicationContext private val context: Context,
) : SettingsUseCase {

    private val lightboxPhotoDiskCacheSize = "imageDiskCacheSize" // cannot change value for backwards compatibility
    private val lightboxPhotoDiskCacheSizeDefault = 500
    private val lightboxPhotoMemCacheSize = "imageDiskCacheSize"  // cannot change value for backwards compatibility
    private val lightboxPhotoMemCacheSizeDefault = 200
    private val thumbnailDiskCacheSize = "thumbnailDiskCacheSize"
    private val thumbnailDiskCacheSizeDefault = 500
    private val thumbnailMemCacheSize = "thumbnailDiskCacheSize"
    private val thumbnailMemCacheSizeDefault = 200
    private val videoDiskCacheSize = "videoDiskCacheSize"
    private val videoDiskCacheSizeDefault = 700
    private val feedSyncFrequency = "feedSyncFrequency"
    private val feedSyncFrequencyDefault = 12
    private val feedDaysToRefresh = "feedDaysToRefresh"
    private val feedDaysToRefreshDefault = 3
    private val shouldPerformPeriodicFeedSync = "shouldPerformPeriodicFeedSync"
    private val shouldPerformPeriodicFeedSyncDefault = true
    private val fullSyncNetworkRequirements = "fullSyncNetworkRequirements"
    private val fullSyncNetworkRequirementsDefault = NetworkType.NOT_ROAMING
    private val fullSyncRequiresCharging = "fullSyncRequiresCharging"
    private val fullSyncRequiresChargingDefault = false
    private val themeMode = "themeMode"
    private val themeModeDefault = ThemeMode.default
    private val mapProvider = "mapProvider"
    private val mapProviderDefault = MapProvider.default
    private val searchSuggestionsEnabled = "searchSuggestionsEnabled"
    private val searchSuggestionsEnabledDefault = true
    private val shareRemoveGpsData = "shareRemoveGpsData"
    private val shareRemoveGpsDataDefault = false
    private val showLibrary = "showLibrary"
    private val showLibraryDefault = true
    private val loggingEnabled = "loggingEnabled"
    private val loggingEnabledDefault = false
    private val biometricsRequiredForAppAccess = "biometricsRequiredForAppAccess"
    private val biometricsRequiredForAppAccessDefault = false
    private val biometricsRequiredForHiddenPhotosAccess = "biometricsRequiredForHiddenPhotosAccess"
    private val biometricsRequiredForHiddenPhotosAccessDefault = false
    private val biometricsRequiredForTrashAccess = "biometricsRequiredForTrashAccess"
    private val biometricsRequiredForTrashAccessDefault = false
    private val memoriesEnabled = "memoriesEnabled"
    private val memoriesEnabledDefault = true
    private val animateVideoThumbnails = "animateVideoThumbnails"
    private val animateVideoThumbnailsDefault = true
    private val maxAnimatedVideoThumbnails = "maxAnimatedVideoThumbnails"
    private val maxAnimatedVideoThumbnailsDefault = 3
    private val showBannerAskingForCloudSyncOnFeed = "showBannerAskingForCloudSyncOnFeed"
    private val showBannerAskingForCloudSyncOnFeedDefault = true
    private val showBannerAskingForLocalMediaPermissionsOnFeed = "showBannerAskingForLocalMediaPermissionsOnFeed"
    private val showBannerAskingForLocalMediaPermissionsOnFeedDefault = true
    private val showBannerAskingForLocalMediaPermissionsOnHeatmap = "showBannerAskingForLocalMediaPermissionsOnHeatmap"
    private val showBannerAskingForLocalMediaPermissionsOnHeatmapDefault = true
    private val feedMediaItemSyncDisplay = "feedMediaItemSyncDisplay"
    private val feedMediaItemSyncDisplayDefault = FeedMediaItemSyncDisplay.default
    private val shouldShowFeedSyncProgress = "shouldShowFeedSyncProgress"
    private val shouldShowFeedSyncProgressDefault = false
    private val shouldShowFeedDetailsSyncProgress = "shouldShowFeedDetailsSyncProgress"
    private val shouldShowFeedDetailsSyncProgressDefault = false
    private val shouldShowPrecacheProgress = "shouldShowPrecacheProgress"
    private val shouldShowPrecacheProgressDefault = false
    private val shouldShowLocalSyncProgress = "shouldShowLocalSyncProgress"
    private val shouldShowLocalSyncProgressDefault = false
    private val autoHideFeedNavOnScroll = "autoHideFeedNavOnScroll"
    private val autoHideFeedNavOnScrollDefault = true

    override fun getLightboxPhotoDiskCacheMaxLimit(): Int =
        getCache(lightboxPhotoDiskCacheSize, lightboxPhotoDiskCacheSizeDefault)
    override fun getLightboxPhotoMemCacheMaxLimit(): Int =
        getCache(lightboxPhotoMemCacheSize, lightboxPhotoMemCacheSizeDefault)
    override fun getThumbnailDiskCacheMaxLimit(): Int =
        getCache(thumbnailDiskCacheSize, thumbnailDiskCacheSizeDefault)
    override fun getThumbnailMemCacheMaxLimit(): Int =
        getCache(thumbnailMemCacheSize, thumbnailMemCacheSizeDefault)
    override fun getVideoDiskCacheMaxLimit(): Int =
        getCache(videoDiskCacheSize, videoDiskCacheSizeDefault)
    override fun getFeedSyncFrequency(): Int =
        get(feedSyncFrequency, feedSyncFrequencyDefault)
    override fun getFeedDaysToRefresh(): Int =
        get(feedDaysToRefresh, feedDaysToRefreshDefault)
    override fun getFullSyncNetworkRequirements(): NetworkType =
        get(fullSyncNetworkRequirements, fullSyncNetworkRequirementsDefault)
    override fun getFullSyncRequiresCharging(): Boolean =
        get(fullSyncRequiresCharging, fullSyncRequiresChargingDefault)
    override fun getShouldPerformPeriodicFullSync(): Boolean =
        get(shouldPerformPeriodicFeedSync, shouldPerformPeriodicFeedSyncDefault)
    override fun getShareRemoveGpsData(): Boolean =
        get(shareRemoveGpsData, shareRemoveGpsDataDefault)
    override fun getShowLibrary(): Boolean =
        get(showLibrary, showLibraryDefault)
    override fun getMapProvider(): MapProvider =
        get(mapProvider, mapProviderDefault).mapToAvailable()
    override fun getAvailableMapProviders(): Set<MapProvider> = MapProvider.entries
        .map { it.mapToAvailable() }.toSet()
    override fun getLoggingEnabled(): Boolean =
        get(loggingEnabled, loggingEnabledDefault)
    override fun getBiometricsRequiredForAppAccess(): Boolean =
        get(biometricsRequiredForAppAccess, biometricsRequiredForAppAccessDefault)
    override fun getBiometricsRequiredForHiddenPhotosAccess(): Boolean =
        get(biometricsRequiredForHiddenPhotosAccess, biometricsRequiredForHiddenPhotosAccessDefault)
    override fun getBiometricsRequiredForTrashAccess(): Boolean =
        get(biometricsRequiredForTrashAccess, biometricsRequiredForTrashAccessDefault)
    override fun getMemoriesEnabled(): Boolean =
        get(memoriesEnabled, memoriesEnabledDefault)
    override fun getAnimateVideoThumbnails(): Boolean =
        get(animateVideoThumbnails, animateVideoThumbnailsDefault)
    override fun getMaxAnimatedVideoThumbnails(): Int =
        get(maxAnimatedVideoThumbnails, maxAnimatedVideoThumbnailsDefault)
    override fun getShowBannerAskingForCloudSyncOnFeed(): Boolean =
        get(showBannerAskingForCloudSyncOnFeed, showBannerAskingForCloudSyncOnFeedDefault)
    override fun getShowBannerAskingForLocalMediaPermissionsOnFeed(): Boolean =
        get(showBannerAskingForLocalMediaPermissionsOnFeed, showBannerAskingForLocalMediaPermissionsOnFeedDefault)
    override fun getShowBannerAskingForLocalMediaPermissionsOnHeatmap(): Boolean =
        get(showBannerAskingForLocalMediaPermissionsOnHeatmap, showBannerAskingForLocalMediaPermissionsOnHeatmapDefault)
    override suspend fun getFeedMediaItemSyncDisplay(): FeedMediaItemSyncDisplay = when {
        welcomeUseCase.getWelcomeStatus().hasRemoteAccess -> get(feedMediaItemSyncDisplay, feedMediaItemSyncDisplayDefault)
        else -> ALWAYS_OFF
    }
    override fun getShouldShowFeedSyncProgress(): Boolean =
        get(shouldShowFeedSyncProgress, shouldShowFeedSyncProgressDefault)
    override fun getShouldShowFeedDetailsSyncProgress(): Boolean =
        get(shouldShowFeedDetailsSyncProgress, shouldShowFeedDetailsSyncProgressDefault)
    override fun getShouldShowPrecacheProgress(): Boolean =
        get(shouldShowPrecacheProgress, shouldShowPrecacheProgressDefault)
    override fun getShouldShowLocalSyncProgress(): Boolean =
        get(shouldShowLocalSyncProgress, shouldShowLocalSyncProgressDefault)
    override fun getAutoHideFeedNavOnScroll(): Boolean =
        get(autoHideFeedNavOnScroll, autoHideFeedNavOnScrollDefault)

    override fun observeLightboxPhotoDiskCacheMaxLimit(): Flow<Int> =
        observeCache(lightboxPhotoDiskCacheSize, lightboxPhotoDiskCacheSizeDefault)
    override fun observeLightboxPhotoMemCacheMaxLimit(): Flow<Int> =
        observeCache(lightboxPhotoMemCacheSize, lightboxPhotoMemCacheSizeDefault)
    override fun observeThumbnailDiskCacheMaxLimit(): Flow<Int> =
        observeCache(thumbnailDiskCacheSize, thumbnailMemCacheSizeDefault)
    override fun observeThumbnailMemCacheMaxLimit(): Flow<Int> =
        observeCache(thumbnailMemCacheSize, thumbnailMemCacheSizeDefault)
    override fun observeVideoDiskCacheMaxLimit(): Flow<Int> =
        observeCache(videoDiskCacheSize, videoDiskCacheSizeDefault)
    override fun observeFeedSyncFrequency(): Flow<Int> =
        observe(feedSyncFrequency, feedSyncFrequencyDefault)
    override fun observeFeedDaysToRefresh(): Flow<Int> =
        observe(feedDaysToRefresh, feedDaysToRefreshDefault)
    override fun observeFullSyncNetworkRequirements(): Flow<NetworkType> =
        observe(fullSyncNetworkRequirements, fullSyncNetworkRequirementsDefault)
    override fun observeFullSyncRequiresCharging(): Flow<Boolean> =
        observe(fullSyncRequiresCharging, fullSyncRequiresChargingDefault)
    override fun observeThemeMode(): Flow<ThemeMode> =
        observe(themeMode, themeModeDefault)
    override fun observeSearchSuggestionsEnabledMode(): Flow<Boolean> =
        observe(searchSuggestionsEnabled, searchSuggestionsEnabledDefault)
    override fun observeShareRemoveGpsData(): Flow<Boolean> =
        observe(shareRemoveGpsData, shareRemoveGpsDataDefault)
    override fun observeShowLibrary(): Flow<Boolean> =
        observe(showLibrary, showLibraryDefault)
    @OptIn(DelicateCoroutinesApi::class)
    override fun observeThemeModeState(): StateFlow<ThemeMode> = GlobalScope.async {
        observeThemeMode().stateIn(
            CoroutineScope(Dispatchers.IO)
        )
    }.asCompletableFuture().join()
    override fun observeMapProvider(): Flow<MapProvider> =
        observe(mapProvider, mapProviderDefault)
            .map { it.mapToAvailable() }
    override fun observeLoggingEnabled(): Flow<Boolean> =
        observe(loggingEnabled, loggingEnabledDefault)
    override fun observeBiometricsRequiredForAppAccess(): Flow<Boolean> =
        observe(biometricsRequiredForAppAccess, biometricsRequiredForAppAccessDefault)
    override fun observeBiometricsRequiredForHiddenPhotosAccess(): Flow<Boolean> =
        observe(biometricsRequiredForHiddenPhotosAccess, biometricsRequiredForHiddenPhotosAccessDefault)
    override fun observeBiometricsRequiredForTrashAccess(): Flow<Boolean> =
        observe(biometricsRequiredForTrashAccess, biometricsRequiredForTrashAccessDefault)
    override fun observeMemoriesEnabled(): Flow<Boolean> =
        observe(memoriesEnabled, memoriesEnabledDefault)
    override fun observeAnimateVideoThumbnails(): Flow<Boolean> =
        observe(animateVideoThumbnails, animateVideoThumbnailsDefault)
    override fun observeMaxAnimatedVideoThumbnails(): Flow<Int> =
        observe(maxAnimatedVideoThumbnails, maxAnimatedVideoThumbnailsDefault)
    override fun observeFeedMediaItemSyncDisplay(): Flow<FeedMediaItemSyncDisplay> = welcomeUseCase.flow(
        withRemoteAccess = observe(feedMediaItemSyncDisplay, feedMediaItemSyncDisplayDefault),
        withoutRemoteAccess = flowOf(ALWAYS_OFF)
    )
    override fun observeShouldShowFeedSyncProgress(): Flow<Boolean> =
        observe(shouldShowFeedSyncProgress, shouldShowFeedSyncProgressDefault)
    override fun observeShouldShowFeedDetailsSyncProgress(): Flow<Boolean> =
        observe(shouldShowFeedDetailsSyncProgress, shouldShowFeedDetailsSyncProgressDefault)
    override fun observeShouldShowPrecacheProgress(): Flow<Boolean> =
        observe(shouldShowPrecacheProgress, shouldShowPrecacheProgressDefault)
    override fun observeShouldShowLocalSyncProgress(): Flow<Boolean> =
        observe(shouldShowLocalSyncProgress, shouldShowLocalSyncProgressDefault)
    override fun observeAutoHideFeedNavOnScroll(): Flow<Boolean> =
        observe(autoHideFeedNavOnScroll, autoHideFeedNavOnScrollDefault)

    override fun setLightboxPhotoDiskCacheMaxLimit(sizeInMb: Int) {
        set(lightboxPhotoDiskCacheSize, sizeInMb)
    }

    override fun setLightboxPhotoMemCacheMaxLimit(sizeInMb: Int) {
        set(lightboxPhotoMemCacheSize, sizeInMb)
    }

    override fun setThumbnailDiskCacheMaxLimit(sizeInMb: Int) {
        set(thumbnailDiskCacheSize, sizeInMb)
    }

    override fun setThumbnailMemCacheMaxLimit(sizeInMb: Int) {
        set(thumbnailMemCacheSize, sizeInMb)
    }

    override fun setVideoDiskCacheMaxLimit(sizeInMb: Int) {
        set(videoDiskCacheSize, sizeInMb)
    }

    override fun setFeedSyncFrequency(frequency: Int) {
        set(feedSyncFrequency, frequency)
    }

    override fun setFeedFeedDaysToRefresh(days: Int) {
        set(feedDaysToRefresh, days)
    }

    override fun setFullSyncNetworkRequirements(networkType: NetworkType) {
        set(fullSyncNetworkRequirements, networkType)
    }

    override fun setFullSyncRequiresCharging(requiresCharging: Boolean) {
        set(fullSyncRequiresCharging, requiresCharging)
    }

    override fun setShouldPerformPeriodicFullSync(perform: Boolean) {
        set(shouldPerformPeriodicFeedSync, perform)
    }

    override fun setThemeMode(mode: ThemeMode) {
        set(themeMode, mode)
    }

    override fun setSearchSuggestionsEnabled(enabled: Boolean) {
        set(searchSuggestionsEnabled, enabled)
    }

    override fun setShareRemoveGpsData(enabled: Boolean) {
        set(shareRemoveGpsData, enabled)
    }

    override fun setShowLibrary(show: Boolean) {
        set(showLibrary, show)
    }

    override fun setMapProvider(provider: MapProvider) {
        set(mapProvider, provider.mapToAvailable())
    }

    override fun setLoggingEnabled(enabled: Boolean) {
        set(loggingEnabled, enabled)
        Log.enabled = enabled
    }

    override fun setBiometricsRequiredForAppAccess(required: Boolean) {
        set(biometricsRequiredForAppAccess, required)
    }

    override fun setBiometricsRequiredForHiddenPhotosAccess(required: Boolean) {
        set(biometricsRequiredForHiddenPhotosAccess, required)
    }

    override fun setBiometricsRequiredForTrashAccess(required: Boolean) {
        set(biometricsRequiredForTrashAccess, required)
    }

    override fun setMemoriesEnabled(enabled: Boolean) {
        set(memoriesEnabled, enabled)
    }

    override fun setAnimateVideoThumbnails(animate: Boolean) {
        set(animateVideoThumbnails, animate)
    }

    override fun setMaxAnimatedVideoThumbnails(max: Int) {
        set(maxAnimatedVideoThumbnails, max)
    }

    override fun setShowBannerAskingForCloudSyncOnFeed(show: Boolean) {
        set(showBannerAskingForCloudSyncOnFeed, show)
    }

    override fun setShowBannerAskingForLocalMediaPermissionsOnFeed(show: Boolean) {
        set(showBannerAskingForLocalMediaPermissionsOnFeed, show)
    }

    override fun setShowBannerAskingForLocalMediaPermissionsOnHeatmap(show: Boolean) {
        set(showBannerAskingForLocalMediaPermissionsOnHeatmap, show)
    }

    override fun setFeedMediaItemSyncDisplay(display: FeedMediaItemSyncDisplay) {
        set(feedMediaItemSyncDisplay, display)
    }

    override fun setShouldShowFeedSyncProgress(show: Boolean) {
        set(shouldShowFeedSyncProgress, show)
    }

    override fun setShouldShowFeedDetailsSyncProgress(show: Boolean) {
        set(shouldShowFeedDetailsSyncProgress, show)
    }

    override fun setShouldShowPrecacheProgress(show: Boolean) {
        set(shouldShowPrecacheProgress, show)
    }

    override fun setShouldShowLocalSyncProgress(show: Boolean) {
        set(shouldShowLocalSyncProgress, show)
    }

    override fun setAutoHideFeedNavOnScroll(autoHide: Boolean) {
        set(autoHideFeedNavOnScroll, autoHide)
    }

    private fun MapProvider.mapToAvailable(): MapProvider =
        when {
            this == Google && !googlePlayAvailable() -> MapBox
            else -> this
        }

    private fun googlePlayAvailable() =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) in setOf(
            SUCCESS, SERVICE_VERSION_UPDATE_REQUIRED, SIGN_IN_REQUIRED, SERVICE_UPDATING
        )

    private fun getCache(key: String, defaultValue: Int) = get(key, defaultValue).coerceAtLeast(
        minCacheSize
    )
    private inline fun <reified T: Enum<T>> get(key: String, defaultValue: T): T =
        preferences.get(key, defaultValue)
    private inline fun <reified T> get(key: String, defaultValue: T): T =
        preferences.get(key, defaultValue)
    private inline fun <reified T: Enum<T>> observe(key: String, defaultValue: T): Flow<T> =
        preferences.observe(key, defaultValue)
    private fun observeCache(key: String, defaultValue: Int): Flow<Int> =
        observe(key, defaultValue).map { it.coerceAtLeast(minCacheSize) }
    private inline fun <reified T> observe(key: String, defaultValue: T): Flow<T> =
        preferences.observe(key, defaultValue)
    private inline fun <reified T: Enum<T>> set(key: String, value: T) {
        preferences.set(key, value)
    }
    private inline fun <reified T> set(key: String, value: T) {
        preferences.set(key, value)
    }
}
