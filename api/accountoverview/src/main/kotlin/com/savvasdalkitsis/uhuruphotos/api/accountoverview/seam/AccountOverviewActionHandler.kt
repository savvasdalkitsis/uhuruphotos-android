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
package com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam

import com.savvasdalkitsis.uhuruphotos.api.account.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.EditServer
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.Load
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.LogOut
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.SettingsClick
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction.UserBadgePressed
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffect.NavigateToServerEdit
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewMutation.HideAccountOverview
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewMutation.HideLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewMutation.ShowAccountOverview
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewMutation.ShowLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewMutation.UserBadgeUpdate
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountOverviewActionHandler @Inject constructor(
    private val accountUseCase: AccountUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
) : ActionHandler<AccountOverviewState, AccountOverviewEffect, AccountOverviewAction, AccountOverviewMutation> {

    override fun handleAction(
        state: AccountOverviewState,
        action: AccountOverviewAction,
        effect: suspend (AccountOverviewEffect) -> Unit
    ): Flow<AccountOverviewMutation> = when (action) {
        Load -> userBadgeUseCase.getUserBadgeState()
            .map(::UserBadgeUpdate)
        UserBadgePressed -> flowOf(ShowAccountOverview)
        DismissAccountOverview -> flowOf(HideAccountOverview)
        AskToLogOut -> flowOf(ShowLogOutConfirmation)
        DismissLogOutDialog -> flowOf(HideLogOutConfirmation)
        LogOut -> flow {
            accountUseCase.logOut()
            effect(ReloadApp)
        }
        EditServer -> flow {
            emit(HideAccountOverview)
            effect(NavigateToServerEdit)
        }
        SettingsClick -> flow {
            emit(HideAccountOverview)
            effect(NavigateToSettings)
        }
    }
}