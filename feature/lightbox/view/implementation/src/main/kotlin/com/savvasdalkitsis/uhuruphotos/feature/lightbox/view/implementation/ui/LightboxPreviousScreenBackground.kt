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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.PullToDismissState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.LocalScreenshotState

@Composable
internal fun LightboxPreviousScreenBackground(
    dismissState: PullToDismissState,
) {
    LocalScreenshotState.current.bitmapState.value?.let { bitmap ->
        if (dismissState.progress > 0f) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(bitmap = remember {
                    bitmap.asImageBitmap()
                }, contentDescription = null)
            }
        }
    }
}