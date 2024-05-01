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
import com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING
import com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
import com.google.android.gms.common.ConnectionResult.SIGN_IN_REQUIRED
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.ALWAYS_OFF
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.flow
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.MapBox
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SettingsUIUseCase(
    private val preferences: Preferences,
    private val welcomeUseCase: WelcomeUseCase,
    private val context: Context,
) : SettingsUIUseCase {

    private val themeMode = "themeMode"
    private val themeModeDefault = ThemeMode.default
    private val mapProvider = "mapProvider"
    private val mapProviderDefault = MapProvider.default
    private val searchSuggestionsEnabled = "searchSuggestionsEnabled"
    private val searchSuggestionsEnabledDefault = true
    private val showLibrary = "showLibrary"
    private val showLibraryDefault = true
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
    private val shouldShowLocalSyncProgressDefault = true
    private val autoHideFeedNavOnScroll = "autoHideFeedNavOnScroll"
    private val autoHideFeedNavOnScrollDefault = true
    private val collageSpacing = "collageSpacing"
    private val collageSpacingDefault = 2
    private val collageSpacingIncludeEdges = "collageSpacingIncludeEdges"
    private val collageSpacingIncludeEdgesDefault = false
    private val collageShape = "collageShape"
    private val collageShapeDefault = CollageShape.default

    override fun getShowLibrary(): Boolean =
        get(showLibrary, showLibraryDefault)
    override fun getMapProvider(): MapProvider =
        get(mapProvider, mapProviderDefault).mapToAvailable()
    override fun getAvailableMapProviders(): Set<MapProvider> = MapProvider.entries
        .map { it.mapToAvailable() }.toSet()
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
    override fun getCollageSpacing(): Int =
        get(collageSpacing, collageSpacingDefault)
    override fun getCollageShape(): CollageShape =
        get(collageShape, collageShapeDefault)
    override fun getCollageSpacingIncludeEdges(): Boolean =
        get(collageSpacingIncludeEdges, collageSpacingIncludeEdgesDefault)

    override fun observeThemeMode(): Flow<ThemeMode> =
        observe(themeMode, themeModeDefault)
    override fun getThemeMode(): ThemeMode =
        get(themeMode, themeModeDefault)
    override fun observeSearchSuggestionsEnabledMode(): Flow<Boolean> =
        observe(searchSuggestionsEnabled, searchSuggestionsEnabledDefault)
    override fun observeShowLibrary(): Flow<Boolean> =
        observe(showLibrary, showLibraryDefault)
    override fun observeMapProvider(): Flow<MapProvider> =
        observe(mapProvider, mapProviderDefault)
            .map { it.mapToAvailable() }
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
    override fun observeCollageSpacing(): Flow<Int> =
        observe(collageSpacing, collageSpacingDefault)
    override fun observeCollageShape(): Flow<CollageShape> =
        observe(collageShape, collageShapeDefault)
    override fun observeCollageSpacingIncludeEdges(): Flow<Boolean> =
        observe(collageSpacingIncludeEdges, collageSpacingIncludeEdgesDefault)

    override fun setThemeMode(mode: ThemeMode) {
        set(themeMode, mode)
    }

    override fun setSearchSuggestionsEnabled(enabled: Boolean) {
        set(searchSuggestionsEnabled, enabled)
    }

    override fun setShowLibrary(show: Boolean) {
        set(showLibrary, show)
    }

    override fun setMapProvider(provider: MapProvider) {
        set(mapProvider, provider.mapToAvailable())
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

    override fun setCollageSpacing(spacing: Int) {
        set(collageSpacing, spacing)
    }

    override fun setCollageShape(shape: CollageShape) {
        set(collageShape, shape)
    }

    override fun setCollageSpacingIncludeEdges(include: Boolean) {
        set(collageSpacingIncludeEdges, include)
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

    private inline fun <reified T: Enum<T>> get(key: String, defaultValue: T): T =
        preferences.get(key, defaultValue)
    private inline fun <reified T> get(key: String, defaultValue: T): T =
        preferences.get(key, defaultValue)
    private inline fun <reified T: Enum<T>> observe(key: String, defaultValue: T): Flow<T> =
        preferences.observe(key, defaultValue)
    private inline fun <reified T> observe(key: String, defaultValue: T): Flow<T> =
        preferences.observe(key, defaultValue)
    private inline fun <reified T: Enum<T>> set(key: String, value: T) {
        preferences.set(key, value)
    }
    private inline fun <reified T> set(key: String, value: T) {
        preferences.set(key, value)
    }
}
