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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import kotlinx.parcelize.Parcelize
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.media_heatmap

@Parcelize
data object HeatMapCollageDisplayState : CollageDisplayState {
    override val miniIcons: Boolean = false
    override val compactColumnsPortrait = 5
    override val compactColumnsLandscape = 3
    override val wideColumnsPortrait = 7
    override val wideColumnsLandscape = 3
    override val iconResource: DrawableResource
        get() = throw IllegalStateException("This is not used")
    override val maintainAspectRatio = false
    override val allowsAnimatedVideoThumbnails = false
    override val friendlyName = string.media_heatmap
    override val zoomIn get() = HeatMapCollageDisplayState
    override val zoomOut get() = HeatMapCollageDisplayState
    override val allowsPinchGestures = false
    override val usingStaggeredGrid = false
}