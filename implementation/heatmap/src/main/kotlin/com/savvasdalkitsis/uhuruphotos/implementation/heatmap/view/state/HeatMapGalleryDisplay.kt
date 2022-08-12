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
package com.savvasdalkitsis.uhuruphotos.implementation.heatmap.view.state

import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryDisplay

object HeatMapGalleryDisplay : GalleryDisplay {
    override val miniIcons: Boolean = false
    override val compactColumnsPortrait = 5
    override val compactColumnsLandscape = 3
    override val wideColumnsPortrait = 7
    override val wideColumnsLandscape = 3
    override val shouldAddEmptyPhotosInRows = true
    override val iconResource: Int
        get() = throw IllegalStateException("This is not used")
    override val maintainAspectRatio = false
    override val friendlyName = 0
    override val zoomIn get() = HeatMapGalleryDisplay
    override val zoomOut get() = HeatMapGalleryDisplay
    override val allowsPinchGestures = false
}