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

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SyncUseCase @Inject constructor(
    private val welcomeUseCase: WelcomeUseCase,
    @PlainTextPreferences
    private val preferences: Preferences,
    private val feedUseCase: FeedUseCase,
    private val uploadsUseCase: UploadsUseCase,
) : SyncUseCase {

    private val enabledKey = "syncEnabledKey"

    override fun observeSyncEnabled(): Flow<Boolean> = combine(
        welcomeUseCase.observeWelcomeStatus(),
        preferences.observe(enabledKey, false)
    ) { welcomeStatus, pref ->
        welcomeStatus.allGranted && pref
    }

    override fun setSyncEnabled(enabled: Boolean) {
        preferences.set(enabledKey, enabled)
    }

    override fun observePendingItems(): Flow<List<UploadItem>> =
        combine(
            observeSyncEnabled(),
            feedUseCase.observeFeed(FeedFetchTypeModel.ALL, loadSmallInitialChunk = false),
            uploadsUseCase.observeUploadsInFlight(),
        )
        { enabled, feed, existingUploads ->
            if (!enabled)
                emptyList()
            else
                (feed.flatMap { it.mediaItems }
                    .mapNotNull { item -> item.id.findLocals.firstOrNull()?.takeIf { item.id.findRemote == null } }
                    .map { item ->
                        UploadItem(item.value, item.contentUri)
                    } + existingUploads.jobs.map { item ->
                        UploadItem(item.localItemId, item.contentUri)
                    }
                ).distinct()
        }.distinctUntilChanged()

}