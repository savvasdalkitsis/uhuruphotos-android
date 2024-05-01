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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.module.AccountModule
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.api.navigation.DiscoverNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.module.DiscoverModule
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.DiscoverActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.Initialise
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either

typealias DiscoverCompositeState = Pair<DiscoverState, AccountOverviewState>
typealias DiscoverCompositeAction = Either<DiscoverAction, AccountOverviewAction>

class DiscoverViewModel(
    discoverActionsContext: DiscoverActionsContext = DiscoverModule.discoverActionsContext,
    accountOverviewActionsContext: AccountOverviewActionsContext = AccountModule.accountOverviewActionsContext,
) : NavigationViewModel<DiscoverCompositeState, DiscoverCompositeAction, DiscoverNavigationRoute>(
    CompositeActionHandler(
        ActionHandlerWithContext(discoverActionsContext),
        ActionHandlerWithContext(accountOverviewActionsContext),
    ),
    DiscoverState() to AccountOverviewState()
) {

    override fun onRouteSet(route: DiscoverNavigationRoute) {
        action(Either.Left(Initialise))
        action(Either.Right(Load))
    }
}