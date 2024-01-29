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

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.minCacheSize
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class SettingsUseCase @Inject constructor(
    @PlainTextPreferences
    private val preferences: Preferences,
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
    private val shareRemoveGpsData = "shareRemoveGpsData"
    private val shareRemoveGpsDataDefault = false
    private val loggingEnabled = "loggingEnabled"
    private val loggingEnabledDefault = false
    private val sendDatabaseEnabled = "sendDatabaseEnabled"
    private val sendDatabaseEnabledDefault = false
    private val biometricsRequiredForAppAccess = "biometricsRequiredForAppAccess"
    private val biometricsRequiredForAppAccessDefault = false
    private val biometricsRequiredForHiddenPhotosAccess = "biometricsRequiredForHiddenPhotosAccess"
    private val biometricsRequiredForHiddenPhotosAccessDefault = false
    private val biometricsRequiredForTrashAccess = "biometricsRequiredForTrashAccess"
    private val biometricsRequiredForTrashAccessDefault = false

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
    override fun getLoggingEnabled(): Boolean =
        get(loggingEnabled, loggingEnabledDefault)
    override fun getSendDatabaseEnabled(): Boolean =
        get(sendDatabaseEnabled, sendDatabaseEnabledDefault)
    override fun getBiometricsRequiredForAppAccess(): Boolean =
        get(biometricsRequiredForAppAccess, biometricsRequiredForAppAccessDefault)
    override fun getBiometricsRequiredForHiddenPhotosAccess(): Boolean =
        get(biometricsRequiredForHiddenPhotosAccess, biometricsRequiredForHiddenPhotosAccessDefault)
    override fun getBiometricsRequiredForTrashAccess(): Boolean =
        get(biometricsRequiredForTrashAccess, biometricsRequiredForTrashAccessDefault)

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
    override fun observeShareRemoveGpsData(): Flow<Boolean> =
        observe(shareRemoveGpsData, shareRemoveGpsDataDefault)
    override fun observeLoggingEnabled(): Flow<Boolean> =
        observe(loggingEnabled, loggingEnabledDefault)
    override fun observeSendDatabaseEnabled(): Flow<Boolean> =
        observe(sendDatabaseEnabled, sendDatabaseEnabledDefault)
    override fun observeBiometricsRequiredForAppAccess(): Flow<Boolean> =
        observe(biometricsRequiredForAppAccess, biometricsRequiredForAppAccessDefault)
    override fun observeBiometricsRequiredForHiddenPhotosAccess(): Flow<Boolean> =
        observe(biometricsRequiredForHiddenPhotosAccess, biometricsRequiredForHiddenPhotosAccessDefault)
    override fun observeBiometricsRequiredForTrashAccess(): Flow<Boolean> =
        observe(biometricsRequiredForTrashAccess, biometricsRequiredForTrashAccessDefault)

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

    override fun setShareRemoveGpsData(enabled: Boolean) {
        set(shareRemoveGpsData, enabled)
    }

    override fun setLoggingEnabled(enabled: Boolean) {
        set(loggingEnabled, enabled)
        Log.enabled = enabled
    }

    override fun setSendDatabaseEnabled(enabled: Boolean) {
        set(sendDatabaseEnabled, enabled)
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
