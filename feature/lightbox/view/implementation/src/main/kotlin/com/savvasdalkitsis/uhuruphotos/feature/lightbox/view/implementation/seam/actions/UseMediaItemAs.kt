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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.flow

data object UseMediaItemAs : LightboxAction() {

    override fun LightboxActionsContext.handle(
        state: LightboxState
    ) = flow<LightboxMutation> {
        when (val id = state.currentMediaItem.id.preferLocal) {
            is MediaIdModel.LocalIdModel -> use(id)
            is MediaIdModel.RemoteIdModel -> {
                val serverUrl = serverUseCase.getServerUrl()
                if (serverUrl != null) {
                    use(id, serverUrl)
                } else {
                    toaster.show(R.string.general_error)
                }
            }
            else -> toaster.show(R.string.general_error)
        }
    }

    context(LightboxActionsContext)
    private suspend fun use(id: MediaIdModel<*>, serverUrl: String? = null) {
        shareUseCase.usePhotoAs(id.fullResUri(serverUrl))
    }

}
