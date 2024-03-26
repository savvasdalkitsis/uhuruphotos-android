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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.UpPressed
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.BackPressHandler
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.ZoomableState

@Composable
internal fun LightboxBackHandler(
    showingActionsOverlay: Boolean,
    scrollState: ScrollState,
    zoomableState: ZoomableState,
    action: (LightboxAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    BackPressHandler {
        scope.launch {
            val scrolledDown = !showingActionsOverlay
            if (scrolledDown) {
                scrollState.animateScrollTo(0)
            } else if ((zoomableState.zoomFraction ?: 0f) > 0f) {
                zoomableState.resetZoom()
            } else {
                action(UpPressed)
            }
        }
    }
}