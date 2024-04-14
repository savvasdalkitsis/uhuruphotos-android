/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions

import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data object ForceReUploadSelectedItems : ProcessingAction() {
    context(ProcessingActionsContext)
    override fun handle(state: ProcessingState): Flow<Mutation<ProcessingState>> = flow {
        val selectedItems = state.items
            .filter { it.selected }
            .map { UploadItem(it.localItemId, it.contentUri) to it.md5 }
        selectedItems.forEach { (item, _) ->
            uploadUseCase.markAsNotProcessing(item.id)
        }
        userUseCase.getRemoteUserOrRefresh().onSuccess { user ->
            selectedItems.forEach { (_, md5) ->
                try {
                    val id = MediaItemHash(md5, user.id).hash
                    remoteMediaUseCase.trashMediaItemNow(id)
                    remoteMediaUseCase.deleteMediaItemNow(id)
                } catch (e: Exception) {
                    log(e)
                }
            }
        }
        uploadUseCase.scheduleUpload(force = true, items = selectedItems
            .map {  (item, _) -> item }
            .toTypedArray()
        )
    }
}