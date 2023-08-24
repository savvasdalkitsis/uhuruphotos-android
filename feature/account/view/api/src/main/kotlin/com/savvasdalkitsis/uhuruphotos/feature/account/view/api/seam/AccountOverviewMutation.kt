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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toPersistentList

sealed class AccountOverviewMutation(
    mutation: Mutation<AccountOverviewState>
) : Mutation<AccountOverviewState> by mutation {

    data class AvatarUpdate(val avatarState: AvatarState) : AccountOverviewMutation({
        it.copy(avatarState = avatarState)
    })

    data class ShowLogin(val show: Boolean) : AccountOverviewMutation({
        it.copy(showLogIn = show)
    })

    data class ShowUserAndServerDetails(val show: Boolean) : AccountOverviewMutation({
        it.copy(showUserAndServerDetails = show)
    })

    data object ShowAccountOverview : AccountOverviewMutation({
        it.copy(showAccountOverview = true)
    })

    data object HideAccountOverview : AccountOverviewMutation({
        it.copy(showAccountOverview = false)
    })

    data object ShowLogOutConfirmation : AccountOverviewMutation({
        it.copy(showLogOutConfirmation = true)
    })

    data object HideLogOutConfirmation : AccountOverviewMutation({
        it.copy(showLogOutConfirmation = false)
    })

    data class ShowJobs(val jobs: List<JobState>) : AccountOverviewMutation({
        it.copy(jobs = jobs.toPersistentList())
    })

    data class ShowUploads(val show: Boolean) : AccountOverviewMutation({
        it.copy(showUploads = show)
    })

    data class ShowUploadsProgress(val inProgress: Boolean) : AccountOverviewMutation({
        it.copy(uploadsInProgress = inProgress)
    })

    data class ShowJobStartDialog(val job: Job) : AccountOverviewMutation({
        it.copy(showJobStartDialog = job)
    })

    data object HideJobDialog : AccountOverviewMutation({
        it.copy(showJobStartDialog = null)
    })
}
