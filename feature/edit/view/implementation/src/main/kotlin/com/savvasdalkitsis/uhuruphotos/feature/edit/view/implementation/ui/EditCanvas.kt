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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.ImageCropped
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.NotifyUserOfError
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import io.moyuru.cropify.AspectRatio
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.CropifyOption
import io.moyuru.cropify.CropifyState

@Composable
internal fun EditCanvas(
    state: EditState,
    cropState: CropifyState,
    action: (EditAction) -> Unit
) {
    when (state.photoUri) {
        null -> UhuruFullLoading()
        else -> {
            val option = remember(state.selectedRatio) {
                CropifyOption(frameAspectRatio = state.selectedRatio?.let {
                    AspectRatio(1/it.ratio)
                })
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Cropify(
                    modifier = Modifier.fillMaxSize(),
                    uri = state.photoUri,
                    state = cropState,
                    option = option,
                    onImageCropped = {
                        action(ImageCropped(it, state.fileName, state.photoUri))
                    },
                    onFailedToLoadImage = {
                        action(NotifyUserOfError)
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 120.dp)
                        .align(Alignment.BottomCenter),
                ) {
                    AnimatedVisibility(
                        visible = state.showCropOptions,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut(),
                    ) {
                        EditCropOptions(action)
                    }
                }
            }

        }
    }
}