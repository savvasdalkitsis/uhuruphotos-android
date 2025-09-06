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
package com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.usecase

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.Feed
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.LOCAL_UPLOADING
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.model.SyncStatus
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.model.SyncStatus.Disabled
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.model.SyncStatus.Enabled
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.model.SyncStatus.NotAvailable
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.CanUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.CannotUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.NotSetUpWithAServer
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.UnableToCheck
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SyncUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val welcomeUseCase: WelcomeUseCase,
    @PlainTextPreferences
    private val preferences: Preferences,
    private val feedUseCase: FeedUseCase,
    private val uploadUseCase: UploadUseCase,
    private val uploadsUseCase: UploadsUseCase,
) : SyncUseCase {

    private val enabledKey = "syncEnabledKey"

    override fun observeSyncStatus(): Flow<SyncStatus> = combine(
        welcomeUseCase.observeWelcomeStatus(),
        preferences.observe(enabledKey, false),
        uploadUseCase.observeSingleCanUpload(),
    ) { welcomeStatus, enabled, canUpload ->
        when (canUpload) {
            CanUpload -> if (welcomeStatus.allGranted && enabled) Enabled else Disabled
            CannotUpload, UnableToCheck, NotSetUpWithAServer -> NotAvailable
        }
    }

    private suspend fun getSyncStatus(): SyncStatus {
        val welcomeStatus = welcomeUseCase.getWelcomeStatus()
        val canUpload = uploadUseCase.getSingleCanUpload()
        val enabled = preferences.get(enabledKey, false)
        return when (canUpload) {
            CanUpload -> if (welcomeStatus.allGranted && enabled) Enabled else Disabled
            CannotUpload, UnableToCheck, NotSetUpWithAServer -> NotAvailable
        }
    }

    override fun setSyncEnabled(enabled: Boolean) {
        preferences.set(enabledKey, enabled)
    }

    override fun observePendingItems(): Flow<List<UploadItem>> =
        combine(
            observeSyncStatus(),
            feedUseCase.observeNewFeed(),
            ::pendingItems
        ).distinctUntilChanged()

    override suspend fun getPendingItems(): List<UploadItem> =
        pendingItems(getSyncStatus(), feedUseCase.getNewFeed())

    private fun pendingItems(
        syncStatus: SyncStatus,
        feed: Feed
    ): List<UploadItem> = when {
        syncStatus != Enabled -> emptyList()
        else -> feed.days
            .flatMap { it.feedItems }
            .filter { it.syncStatus == LOCAL_ONLY || it.syncStatus == LOCAL_UPLOADING }
            .mapNotNull { item ->
                item.uri.localMediaId?.let { id ->
                    UploadItem(
                        id,
                        item.uri.resolve(
                            item.md5sum,
                            "",
                            null,
                            item.isVideo,
                            context,
                            isThumbnail = false
                        )
                    )
                }
            }
            .distinct()
    }

}