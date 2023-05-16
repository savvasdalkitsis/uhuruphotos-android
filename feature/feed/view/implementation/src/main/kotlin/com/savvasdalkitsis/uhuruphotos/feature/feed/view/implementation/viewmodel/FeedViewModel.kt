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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.viewmodel

import androidx.lifecycle.viewModelScope
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.effects.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.LoadFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.effects.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private typealias FeedCompositeState = Pair<FeedState, AccountOverviewState>
private typealias FeedCompositeEffect = Either<FeedEffect, AccountOverviewEffect>
private typealias FeedCompositeAction = Either<FeedAction, AccountOverviewAction>

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    feedActionsContext: FeedActionsContext,
    accountOverviewActionsContext: AccountOverviewActionsContext,
    feedEffectsContext: FeedEffectsContext,
    accountOverviewEffectsContext: AccountOverviewEffectsContext,
) : NavigationViewModel<FeedCompositeState, FeedCompositeEffect, FeedCompositeAction, FeedNavigationRoute>(
    CompositeActionHandler(
        ActionHandlerWithContext(feedActionsContext),
        ActionHandlerWithContext(accountOverviewActionsContext),
    ),
    CompositeEffectHandler(
        EffectHandlerWithContext(feedEffectsContext),
        EffectHandlerWithContext(accountOverviewEffectsContext),
    ),
    FeedState() to AccountOverviewState(),
) {

    init {
        viewModelScope.launch {
            action(Either.Left(LoadFeed))
        }
        viewModelScope.launch {
            action(Either.Right(Load))
        }
    }
}