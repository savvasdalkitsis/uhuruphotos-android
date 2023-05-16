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
package com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.implementation.usecase

import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase.AvatarUseCase
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.BAD
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.GOOD
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AvatarUseCase @Inject constructor(
    private val userUseCase: UserUseCase,
    private val feedWorkScheduler: FeedWorkScheduler,
    private val serverUseCase: ServerUseCase,
) : AvatarUseCase {

    override fun getAvatarState(): Flow<AvatarState> = combine(
        userUseCase.observeUser(),
        feedWorkScheduler.observeFeedRefreshJobStatus(),
        feedWorkScheduler.observePrecacheThumbnailsJobStatus(),
        serverUseCase.observeServerUrl(),
    ) { user, feedSyncStatus, precacheStatus, serverUrl ->
        val statuses = listOf(feedSyncStatus, precacheStatus)
        AvatarState(
            avatarUrl = user.avatar?.let { "$serverUrl$it" },
            syncState = when {
                BLOCKED in statuses || FAILED in statuses -> BAD
                RUNNING in statuses -> IN_PROGRESS
                else -> GOOD
            },
            initials = user.firstName.initial() + user.lastName.initial(),
            userFullName = "${user.firstName} ${user.lastName}",
            serverUrl = serverUrl,
        )
    }

    private fun String?.initial() =
        orEmpty().firstOrNull()?.toString()?.uppercase() ?: ""
}