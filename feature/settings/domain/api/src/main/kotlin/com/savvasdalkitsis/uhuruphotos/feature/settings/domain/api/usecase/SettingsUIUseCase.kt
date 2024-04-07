package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import kotlinx.coroutines.flow.Flow

interface SettingsUIUseCase {

    fun getShowLibrary(): Boolean
    fun getMapProvider(): MapProvider
    fun getAvailableMapProviders(): Set<MapProvider>
    fun getMemoriesEnabled(): Boolean
    fun getAnimateVideoThumbnails(): Boolean
    fun getMaxAnimatedVideoThumbnails(): Int
    fun getShowBannerAskingForCloudSyncOnFeed(): Boolean
    fun getShowBannerAskingForLocalMediaPermissionsOnFeed(): Boolean
    fun getShowBannerAskingForLocalMediaPermissionsOnHeatmap(): Boolean
    suspend fun getFeedMediaItemSyncDisplay(): FeedMediaItemSyncDisplay
    fun getShouldShowFeedSyncProgress(): Boolean
    fun getShouldShowFeedDetailsSyncProgress(): Boolean
    fun getShouldShowPrecacheProgress(): Boolean
    fun getShouldShowLocalSyncProgress(): Boolean
    fun getAutoHideFeedNavOnScroll(): Boolean
    fun getThemeMode(): ThemeMode
    fun getCollageSpacing(): Int
    fun getCollageSpacingIncludeEdges(): Boolean
    fun getCollageShape(): CollageShape

    fun observeThemeMode(): Flow<ThemeMode>
    fun observeSearchSuggestionsEnabledMode(): Flow<Boolean>
    fun observeShowLibrary(): Flow<Boolean>
    fun observeMapProvider(): Flow<MapProvider>
    fun observeMemoriesEnabled(): Flow<Boolean>
    fun observeAnimateVideoThumbnails(): Flow<Boolean>
    fun observeMaxAnimatedVideoThumbnails(): Flow<Int>
    fun observeFeedMediaItemSyncDisplay(): Flow<FeedMediaItemSyncDisplay>
    fun observeShouldShowFeedSyncProgress(): Flow<Boolean>
    fun observeShouldShowFeedDetailsSyncProgress(): Flow<Boolean>
    fun observeShouldShowPrecacheProgress(): Flow<Boolean>
    fun observeShouldShowLocalSyncProgress(): Flow<Boolean>
    fun observeAutoHideFeedNavOnScroll(): Flow<Boolean>
    fun observeCollageSpacing(): Flow<Int>
    fun observeCollageSpacingIncludeEdges(): Flow<Boolean>
    fun observeCollageShape(): Flow<CollageShape>

    fun setThemeMode(mode: ThemeMode)
    fun setSearchSuggestionsEnabled(enabled: Boolean)
    fun setShowLibrary(show: Boolean)
    fun setMapProvider(provider: MapProvider)
    fun setMemoriesEnabled(enabled: Boolean)
    fun setAnimateVideoThumbnails(animate: Boolean)
    fun setMaxAnimatedVideoThumbnails(max: Int)
    fun setShowBannerAskingForCloudSyncOnFeed(show: Boolean)
    fun setShowBannerAskingForLocalMediaPermissionsOnFeed(show: Boolean)
    fun setShowBannerAskingForLocalMediaPermissionsOnHeatmap(show: Boolean)
    fun setFeedMediaItemSyncDisplay(display: FeedMediaItemSyncDisplay)
    fun setShouldShowFeedSyncProgress(show: Boolean)
    fun setShouldShowFeedDetailsSyncProgress(show: Boolean)
    fun setShouldShowPrecacheProgress(show: Boolean)
    fun setShouldShowLocalSyncProgress(show: Boolean)
    fun setAutoHideFeedNavOnScroll(autoHide: Boolean)
    fun setCollageSpacing(spacing: Int)
    fun setCollageSpacingIncludeEdges(include: Boolean)
    fun setCollageShape(shape: CollageShape)
}