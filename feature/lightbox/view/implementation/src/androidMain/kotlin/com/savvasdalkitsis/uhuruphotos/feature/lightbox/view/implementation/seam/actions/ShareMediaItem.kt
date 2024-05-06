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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import kotlinx.coroutines.flow.flow

data object ShareMediaItem : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState
    ) = flow<LightboxMutation> {
        when (val id = state.currentMediaItem.id.preferLocal) {
            is MediaId.Local -> share(id)
            is MediaId.Remote -> {
                val serverUrl = serverUseCase.getServerUrl()
                if (serverUrl != null) {
                    share(id, serverUrl)
                } else {
                    toaster.show(strings.general_error)
                }
            }
            else -> toaster.show(strings.general_error)
        }
    }

    context(LightboxActionsContext)
    private suspend fun share(id: MediaId<*>, serverUrl: String? = null) {
        shareUseCase.share(id.fullResUri(serverUrl))
    }
}