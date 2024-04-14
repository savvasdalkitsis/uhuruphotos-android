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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam

import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.CropRatio
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class EditMutation(
    mutation: Mutation<EditState>,
) : Mutation<EditState> by mutation {

    data object Saving : EditMutation({
        it.copy(
            actionBarActionsEnabled = false,
            isLoading = true,
        )
    })

    data class SetPhotoData(val uri: Uri, val name: String, val timestamp: Long?) : EditMutation({
        it.copy(
            photoUri = uri,
            actionBarActionsEnabled = true,
            isLoading = false,
            fileName = name,
            timestamp = timestamp,
        )
    })

    data class SetCropOptionsVisible(val visible: Boolean) : EditMutation({
        it.copy(showCropOptions = visible)
    })

    data class SelectCropRatio(val ratio: CropRatio) : EditMutation({
        it.copy(selectedRatio = ratio)
    })
}