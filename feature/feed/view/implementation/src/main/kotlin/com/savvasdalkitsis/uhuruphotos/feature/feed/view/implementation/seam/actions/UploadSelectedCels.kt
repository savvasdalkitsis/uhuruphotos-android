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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import kotlinx.coroutines.flow.flow
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_uploading

data object UploadSelectedCels : FeedAction() {
    override fun FeedActionsContext.handle(
        state: FeedState
    ) = flow<FeedMutation> {
        toaster.show(string.media_sync_status_uploading)
        selectionList.clear()
        uploadUseCase.markAsUploading(items = (state.selectedCels.mapNotNull {
            it.mediaItem.id.findLocals.firstOrNull()
        }.map { UploadItem(
            id = it.value,
            contentUri = it.contentUri,
        ) }.toTypedArray()))
    }
}
