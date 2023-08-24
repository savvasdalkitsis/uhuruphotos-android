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

package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AccountOverviewState(
    val showAccountOverview: Boolean = false,
    val avatarState: AvatarState = AvatarState(),
    val showLogOutConfirmation: Boolean = false,
    val showJobStartDialog: Job? = null,
    val showLogIn: Boolean = false,
    val showUserAndServerDetails: Boolean = false,
    val jobs: ImmutableList<JobState> = persistentListOf(),
    val showUploads: Boolean = false,
    val uploadsInProgress: Boolean = false,
)