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

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMediaItemFavourite
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import kotlinx.coroutines.flow.flow
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.error_changing_photo_favourite

data class SetFavourite(val favourite: Boolean) : LightboxAction() {

    override fun LightboxActionsContext.handle(
        state: LightboxState
    ) = flow {
        mediaUseCase.setMediaItemFavourite(state.currentMediaItem.id, favourite)
            .onFailure {
                emit(ShowErrorMessage(string.error_changing_photo_favourite))
            }
            .onSuccess {
                emit(ShowMediaItemFavourite(state.currentMediaItem.id, favourite))
            }
    }

}
