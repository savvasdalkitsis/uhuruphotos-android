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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.effects.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.search.view.api.navigation.SearchNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.Initialise
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.effects.SearchEffect
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private typealias SearchCompositeState = Pair<SearchState, AccountOverviewState>
private typealias SearchCompositeEffect = Either<SearchEffect, AccountOverviewEffect>
private typealias SearchCompositeAction = Either<SearchAction, AccountOverviewAction>

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchActionsContext: SearchActionsContext,
    accountOverviewActionsContext: AccountOverviewActionsContext,
    searchEffectsContext: SearchEffectsContext,
    accountOverviewEffectsContext: AccountOverviewEffectsContext,
) : NavigationViewModel<SearchCompositeState, SearchCompositeEffect, SearchCompositeAction, SearchNavigationRoute>(
    CompositeActionHandler(
        ActionHandlerWithContext(searchActionsContext),
        ActionHandlerWithContext(accountOverviewActionsContext),
    ),
    CompositeEffectHandler(
        EffectHandlerWithContext(searchEffectsContext),
        EffectHandlerWithContext(accountOverviewEffectsContext),
    ),
    SearchState() to AccountOverviewState()
) {

    override fun onRouteSet(route: SearchNavigationRoute) {
        action(Either.Left(Initialise))
        action(Either.Right(Load))
    }
}