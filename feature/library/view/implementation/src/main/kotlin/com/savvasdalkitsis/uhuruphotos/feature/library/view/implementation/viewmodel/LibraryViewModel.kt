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
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewAction
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.handler
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
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
    LibraryState() to AccountOverviewState()
)