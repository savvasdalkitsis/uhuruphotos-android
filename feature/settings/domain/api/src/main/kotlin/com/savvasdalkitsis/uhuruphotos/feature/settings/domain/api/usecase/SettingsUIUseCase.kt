/*
Copyright 2024 Savvas Dalkitsis

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

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import kotlinx.coroutines.flow.Flow

interface SettingsUIUseCase {
    val themeModeDefault get() = ThemeMode.default
    val themeVariantDefault get() = ThemeVariant.default
    val themeContrastDefault get() = ThemeContrast.default
    val collageSpacingDefault get() = 6
    val collageSpacingIncludeEdgesDefault get() = true
    val collageShapeDefault get() = CollageShape.default


    fun getShowLibrary(): Boolean
    fun getMapProvider(): MapProvider
    fun getAvailableMapProviders(): Set<MapProvider>
    fun getMemoriesEnabled(): Boolean
    fun getMemoriesParallaxEnabled(): Boolean
    fun getAnimateVideoThumbnails(): Boolean
    fun getMaxAnimatedVideoThumbnails(): Int
    fun getShowBannerAskingForCloudSyncOnFeed(): Boolean
    fun getShowBannerAskingForLocalMediaPermissionsOnFeed(): Boolean
    fun getShowBannerAskingForLocalMediaPermissionsOnHeatmap(): Boolean
    suspend fun getFeedMediaItemSyncDisplay(): FeedMediaItemSyncDisplayState
    fun getShouldShowFeedSyncProgress(): Boolean
    fun getShouldShowFeedDetailsSyncProgress(): Boolean
    fun getShouldShowPrecacheProgress(): Boolean
    fun getShouldShowLocalSyncProgress(): Boolean
    fun getAutoHideFeedNavOnScroll(): Boolean
    fun getThemeMode(): ThemeMode
    fun getThemeVariant(): ThemeVariant
    fun getThemeContrast(): ThemeContrast
    fun getCollageSpacing(): Int
    fun getCollageSpacingIncludeEdges(): Boolean
    fun getCollageShape(): CollageShape

    fun hasThemeVariantSet(): Boolean

    fun observeThemeMode(): Flow<ThemeMode>
    fun observeThemeVariant(): Flow<ThemeVariant>
    fun observeThemeContrast(): Flow<ThemeContrast>
    fun observeSearchSuggestionsEnabledMode(): Flow<Boolean>
    fun observeShowLibrary(): Flow<Boolean>
    fun observeMapProvider(): Flow<MapProvider>
    fun observeMemoriesEnabled(): Flow<Boolean>
    fun observeMemoriesParallaxEnabled(): Flow<Boolean>
    fun observeAnimateVideoThumbnails(): Flow<Boolean>
    fun observeMaxAnimatedVideoThumbnails(): Flow<Int>
    fun observeFeedMediaItemSyncDisplay(): Flow<FeedMediaItemSyncDisplayState>
    fun observeShouldShowFeedSyncProgress(): Flow<Boolean>
    fun observeShouldShowFeedDetailsSyncProgress(): Flow<Boolean>
    fun observeShouldShowPrecacheProgress(): Flow<Boolean>
    fun observeShouldShowLocalSyncProgress(): Flow<Boolean>
    fun observeAutoHideFeedNavOnScroll(): Flow<Boolean>
    fun observeCollageSpacing(): Flow<Int>
    fun observeCollageSpacingIncludeEdges(): Flow<Boolean>
    fun observeCollageShape(): Flow<CollageShape>

    fun setThemeMode(mode: ThemeMode)
    fun setThemeVariant(variant: ThemeVariant)
    fun setThemeContrast(contrast: ThemeContrast)
    fun setSearchSuggestionsEnabled(enabled: Boolean)
    fun setShowLibrary(show: Boolean)
    fun setMapProvider(provider: MapProvider)
    fun setMemoriesEnabled(enabled: Boolean)
    fun setMemoriesParallaxEnabled(enabled: Boolean)
    fun setAnimateVideoThumbnails(animate: Boolean)
    fun setMaxAnimatedVideoThumbnails(max: Int)
    fun setShowBannerAskingForCloudSyncOnFeed(show: Boolean)
    fun setShowBannerAskingForLocalMediaPermissionsOnFeed(show: Boolean)
    fun setShowBannerAskingForLocalMediaPermissionsOnHeatmap(show: Boolean)
    fun setFeedMediaItemSyncDisplay(display: FeedMediaItemSyncDisplayState)
    fun setShouldShowFeedSyncProgress(show: Boolean)
    fun setShouldShowFeedDetailsSyncProgress(show: Boolean)
    fun setShouldShowPrecacheProgress(show: Boolean)
    fun setShouldShowLocalSyncProgress(show: Boolean)
    fun setAutoHideFeedNavOnScroll(autoHide: Boolean)
    fun setCollageSpacing(spacing: Int)
    fun setCollageSpacingIncludeEdges(include: Boolean)
    fun setCollageShape(shape: CollageShape)
}