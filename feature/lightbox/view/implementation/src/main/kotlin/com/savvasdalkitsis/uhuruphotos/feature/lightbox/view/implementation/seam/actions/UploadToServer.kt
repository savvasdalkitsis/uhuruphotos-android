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
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowCannotCheckUploadStatusDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowCannotUploadDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowUpsellDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.CanUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.CannotUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.NotSetUpWithAServer
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.UnableToCheck
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import kotlinx.coroutines.flow.flow

data class UploadToServer(val mediaItemState: SingleMediaItemState) : LightboxAction() {

    override fun LightboxActionsContext.handle(
        state: LightboxState,
    ) = flow {
        val id = mediaItemState.id.findLocals.firstOrNull()
        if (id != null) {
            emit(Loading)
            when (uploadUseCase.canUpload()) {
                CanUpload -> uploadUseCase.markAsUploading(items = arrayOf(UploadItem(id.value, id.contentUri)))
                CannotUpload -> emit(ShowCannotUploadDialog)
                UnableToCheck -> emit(ShowCannotCheckUploadStatusDialog)
                NotSetUpWithAServer -> emit(ShowUpsellDialog(true))
            }
            emit(FinishedLoading)
        }
    }
}
