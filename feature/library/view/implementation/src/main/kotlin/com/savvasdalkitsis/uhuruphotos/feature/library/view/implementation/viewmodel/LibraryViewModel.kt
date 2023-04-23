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

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.api.navigation.LibraryNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.Load as LoadLibrary
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions.Load as LoadAccount

private typealias LibraryCompositeState = Pair<LibraryState, AccountOverviewState>
private typealias LibraryCompositeAction = Either<LibraryAction, AccountOverviewAction>

@HiltViewModel
internal class LibraryViewModel @Inject constructor(
    libraryActionsContext: LibraryActionsContext,
    accountOverviewActionsContext: AccountOverviewActionsContext,
    accountOverviewEffectHandler: AccountOverviewEffectHandler,
    libraryEffectHandler: LibraryEffectHandler,
) : ViewModel(),
    HasActionableState<LibraryCompositeState, LibraryCompositeAction> by Seam(
        CompositeActionHandler(
            ActionHandlerWithContext(libraryActionsContext),
            ActionHandlerWithContext(accountOverviewActionsContext),
        ),
        CompositeEffectHandler(
            libraryEffectHandler,
            accountOverviewEffectHandler,
        ),
        LibraryState() to AccountOverviewState()
    ),
    HasInitializer<LibraryCompositeAction, LibraryNavigationRoute> {

    override suspend fun initialize(
        initializerData: LibraryNavigationRoute,
        action: (LibraryCompositeAction) -> Unit,
    ) {
        action(Either.Left(LoadLibrary))
        action(Either.Right(LoadAccount))
    }
}