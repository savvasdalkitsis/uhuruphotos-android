/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.ShowUpsellFrom
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItem.TRASH
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.api.navigation.TrashNavigationRoute
import kotlinx.coroutines.flow.flow

data object TrashSelected : LibraryAction() {
    context(LibraryActionsContext) override fun handle(
        state: LibraryState
    ) = flow<LibraryMutation> {
        if (welcomeUseCase.getWelcomeStatus().hasRemoteAccess) {
            navigator.navigateTo(TrashNavigationRoute)
        } else {
            emit(ShowUpsellFrom(TRASH))
        }
    }
}
