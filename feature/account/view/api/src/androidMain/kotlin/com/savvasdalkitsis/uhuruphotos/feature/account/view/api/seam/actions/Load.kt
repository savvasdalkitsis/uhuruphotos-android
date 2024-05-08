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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.AvatarUpdate
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowCloudSync
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowCloudSyncEnabled
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowJobs
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowLogin
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowUploads
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowUploadsProgress
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewMutation.ShowUserAndServerDetails
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.toJobState
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.api.navigation.WelcomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.andThen
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

data object Load : AccountOverviewAction() {
    override fun AccountOverviewActionsContext.handle(
        state: AccountOverviewState
    ) = merge(
        welcomeUseCase.observeWelcomeStatus()
            .onEach {
                if (it.allMissing) {
                    navigator.newRoot(WelcomeNavigationRoute)
                }
            }
            .map {
                ShowLogin(!it.hasRemoteAccess) andThen
                        ShowUserAndServerDetails(it.hasRemoteAccess) andThen
                        ShowUploads(it.hasRemoteAccess) andThen
                        ShowCloudSync(it.hasRemoteAccess)
            },
        avatarUseCase.getAvatarState()
            .map(::AvatarUpdate),
        jobsUseCase.observeJobsStatus().map {
            ShowJobs(it.jobs.toJobState)
        },
        uploadsUseCase.observeUploadsInFlight().map { uploads ->
            ShowUploadsProgress(uploads.inProgress)
        },
        syncUseCase.observeSyncEnabled().map { enabled ->
            ShowCloudSyncEnabled(enabled)
        }
    ).onStart {
        onIO {
            userUseCase.refreshRemoteUser()
        }
    }
}
