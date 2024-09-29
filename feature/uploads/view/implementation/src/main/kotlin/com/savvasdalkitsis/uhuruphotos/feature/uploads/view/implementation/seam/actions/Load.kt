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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam.UploadsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam.UploadsMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam.UploadsMutation.ShowUploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.ui.state.UploadsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data object Load : UploadsAction() {

    override fun UploadsActionsContext.handle(state: UploadsState): Flow<Mutation<UploadsState>> =
        uploadsUseCase.observeUploadsInFlight()
            .map<Uploads, Mutation<UploadsState>> { ShowUploads(it) }
            .onStart { emit(Loading) }
}