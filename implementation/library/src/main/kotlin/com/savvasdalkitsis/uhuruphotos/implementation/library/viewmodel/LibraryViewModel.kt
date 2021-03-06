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
package com.savvasdalkitsis.uhuruphotos.implementation.library.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewActionHandler
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.seam.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.api.seam.Seam
import com.savvasdalkitsis.uhuruphotos.api.seam.SeamViaHandler.Companion.handler
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    libraryActionHandler: LibraryActionHandler,
    accountOverviewActionHandler: AccountOverviewActionHandler,
) : ViewModel(), Seam<
        Pair<LibraryState, AccountOverviewState>,
        Either<LibraryEffect, AccountOverviewEffect>,
        Either<LibraryAction, AccountOverviewAction>,
        Mutation<Pair<LibraryState, AccountOverviewState>>
    > by handler(
        CompositeActionHandler(
            handler1 = libraryActionHandler,
            handler2 = accountOverviewActionHandler,
        ),
        LibraryState() to AccountOverviewState(),
    )