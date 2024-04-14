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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.api.navigation.LibraryNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.Load as LoadAccount
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.Load as LoadLibrary

internal typealias LibraryCompositeState = Pair<LibraryState, AccountOverviewState>
internal typealias LibraryCompositeAction = Either<LibraryAction, AccountOverviewAction>

@HiltViewModel
internal class LibraryViewModel @Inject constructor(
    libraryActionsContext: LibraryActionsContext,
    accountOverviewActionsContext: AccountOverviewActionsContext,
) : NavigationViewModel<LibraryCompositeState, LibraryCompositeAction, LibraryNavigationRoute>(
    CompositeActionHandler(
        ActionHandlerWithContext(libraryActionsContext),
        ActionHandlerWithContext(accountOverviewActionsContext),
    ),
    LibraryState() to AccountOverviewState()
) {

    override fun onRouteSet(route: LibraryNavigationRoute) {
        action(Either.Left(LoadLibrary))
        action(Either.Right(LoadAccount))
    }
}