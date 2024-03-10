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

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase.AvatarUseCase
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.BAD
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.GOOD
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Failed
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.InProgress
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobsStatus
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.usecase.JobsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.User
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.flow
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class AvatarUseCase @Inject constructor(
    private val userUseCase: UserUseCase,
    private val serverUseCase: ServerUseCase,
    private val jobsUseCase: JobsUseCase,
    private val welcomeUseCase: WelcomeUseCase,
) : AvatarUseCase {

    override fun getAvatarState(): Flow<AvatarState> = welcomeUseCase.flow(
        withoutRemoteAccess = jobsUseCase.observeJobsStatusFilteredBySettings().map { jobsStatus ->
            AvatarState(syncState = jobsStatus.syncState())
        },
        withRemoteAccess = combine(
            userUseCase.observeUser().filterIsInstance(User.RemoteUser::class),
            jobsUseCase.observeJobsStatusFilteredBySettings(),
            serverUseCase.observeServerUrl(),
        ) { user, jobsStatus, serverUrl ->
            val model = user.model
            AvatarState(
                avatarUrl = model.avatar?.let { "$serverUrl$it" },
                syncState = jobsStatus.syncState(),
                initials = model.firstName.initial() + model.lastName.initial(),
                userFullName = "${model.firstName} ${model.lastName}",
                serverUrl = serverUrl,
            )
        },
    )

    private fun JobsStatus.syncState() = jobs.values.run {
        when {
            any { it is Failed } -> BAD
            any { it is InProgress } -> IN_PROGRESS
            else -> GOOD
        }
    }

    private fun String?.initial() =
        orEmpty().firstOrNull()?.toString()?.uppercase() ?: ""
}