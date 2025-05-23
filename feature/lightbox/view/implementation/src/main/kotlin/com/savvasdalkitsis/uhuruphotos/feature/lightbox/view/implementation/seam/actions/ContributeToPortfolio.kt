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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import kotlinx.coroutines.flow.flow
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.contributing_to_feed
import uhuruphotos_android.foundation.strings.api.generated.resources.not_contributing_to_feed

data class ContributeToPortfolio(
    val mediaItemState: SingleMediaItemState,
    val contribute: Boolean,
) : LightboxAction() {

    override fun LightboxActionsContext.handle(
        state: LightboxState,
    ) = flow<LightboxMutation> {
        mediaItemState.id.findLocals.firstOrNull()?.let { id ->
            portfolioUseCase.publishItemToPortfolio(id.value, id.folderId, contribute)
            toaster.show(
                if (contribute) string.contributing_to_feed else string.not_contributing_to_feed
            )
        }
    }

}
