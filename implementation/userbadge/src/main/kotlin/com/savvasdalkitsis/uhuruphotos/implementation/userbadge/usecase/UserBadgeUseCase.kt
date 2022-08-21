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
package com.savvasdalkitsis.uhuruphotos.implementation.userbadge.usecase

import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.CANCELLED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.api.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state.SyncState.BAD
import com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state.SyncState.GOOD
import com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state.UserInformationState
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserBadgeUseCase @Inject constructor(
    private val userUseCase: UserUseCase,
    private val albumWorkScheduler: AlbumWorkScheduler,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val serverUseCase: ServerUseCase,
) : UserBadgeUseCase {

    override fun getUserBadgeState(): Flow<UserInformationState> = combine(
        userUseCase.observeUser(),
        albumWorkScheduler.observeAlbumRefreshJobStatus(),
        serverUseCase.observeServerUrl(),
    ) { user, status, serverUrl ->
        UserInformationState(
            avatarUrl = with(remoteMediaUseCase) { user.avatar?.toRemoteUrl() },
            syncState = when (status) {
                BLOCKED, FAILED -> BAD
                CANCELLED, ENQUEUED, SUCCEEDED -> GOOD
                RUNNING -> IN_PROGRESS
            },
            initials = user.firstName.initial() + user.lastName.initial(),
            userFullName = "${user.firstName} ${user.lastName}",
            serverUrl = serverUrl,
        )
    }

    private fun String?.initial() =
        orEmpty().firstOrNull()?.toString()?.uppercase() ?: ""
}

private operator fun WorkInfo.State.plus(other: WorkInfo.State): WorkInfo.State = when {
    this == RUNNING || other == RUNNING -> RUNNING
    this == FAILED || other == FAILED -> FAILED
    this == SUCCEEDED && other == SUCCEEDED -> SUCCEEDED
    else -> this
}
