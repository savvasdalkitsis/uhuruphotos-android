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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.ui.state.UploadsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toImmutableList

sealed class UploadsMutation(
    mutation: Mutation<UploadsState>,
) : Mutation<UploadsState> by mutation {

    data object Loading : UploadsMutation({
        it.copy(isLoading = true)
    })

    data class ShowUploads(val uploads: Uploads) : UploadsMutation({
        it.copy(isLoading = false, jobs = uploads.jobs.toImmutableList())
    })
}