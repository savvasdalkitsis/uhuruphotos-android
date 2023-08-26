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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AboutClicked
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AskToStartJob
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.CancelJob
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.ChangeCloudSync
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.HideJobDialog
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.LogIn
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.LogOut
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.SettingsClick
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.StartJob
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.ViewAllUploads
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.JobPermissionDialog

@Composable
fun AccountOverviewContent(
    state: AccountOverviewState,
    action: (AccountOverviewAction) -> Unit,
) {
    AccountOverviewPopUp(
        state = state,
        onDismiss = { action(DismissAccountOverview) },
        onAboutClicked = { action(AboutClicked) },
        onLoginClicked = { action(LogIn) },
        onLogoutClicked = { action(AskToLogOut) },
        onStartJob = { action(AskToStartJob(it)) },
        onCancelJob = { action(CancelJob(it)) },
        onSettingsClicked = { action(SettingsClick) },
        onViewAllUploadsClicked = { action(ViewAllUploads) },
        onCloudSyncChanged = { action(ChangeCloudSync(it)) },
    )
    if (state.showLogOutConfirmation) {
        LogOutConfirmationDialog(
            onDismiss = { action(DismissLogOutDialog) },
            onLogOut = { action(LogOut) },
        )
    }
    state.showJobStartDialog?.let { job ->
        JobPermissionDialog(
            job = job,
            onStartJob = { action(StartJob(job)) },
            onDismiss = { action(HideJobDialog) }
        )
    }
}