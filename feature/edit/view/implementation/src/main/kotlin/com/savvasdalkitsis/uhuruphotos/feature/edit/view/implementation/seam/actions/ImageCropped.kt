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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditMutation
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class ImageCropped(val image: ImageBitmap, val name: String, val uri: Uri) : EditAction() {
    context(EditActionsContext) override fun handle(
        state: EditState,
        effect: EffectHandler<CommonEffect>
    ): Flow<Mutation<EditState>> = flow {
        emit(EditMutation.Saving)
        if (!localMediaUseCase.savePhoto(image.asAndroidBitmap(), "CROPPED_$name", uri)) {
            toaster.show(R.string.error_editing_photo)
        }
        navigator.navigateBack()
    }
}