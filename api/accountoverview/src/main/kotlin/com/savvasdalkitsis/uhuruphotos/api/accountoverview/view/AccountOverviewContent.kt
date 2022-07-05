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
package com.savvasdalkitsis.uhuruphotos.api.accountoverview.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.api.account.view.LogOutConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.EditServer
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.LogOut
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.SettingsClick
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.state.AccountOverviewState

@Composable
fun AccountOverviewContent(
    state: AccountOverviewState,
    action: (AccountOverviewAction) -> Unit,
) {
    AccountOverviewPopUp(
        visible = state.showAccountOverview,
        userInformationState = state.userInformationState,
        onDismiss = { action(DismissAccountOverview) },
        onLogoutClicked = { action(AskToLogOut) },
        onEditServerClicked = { action(EditServer) },
        onSettingsClicked = { action(SettingsClick) },
    )
    if (state.showLogOutConfirmation) {
        LogOutConfirmationDialog(
            onDismiss = { action(DismissLogOutDialog) },
            onLogOut = { action(LogOut) },
        )
    }
}