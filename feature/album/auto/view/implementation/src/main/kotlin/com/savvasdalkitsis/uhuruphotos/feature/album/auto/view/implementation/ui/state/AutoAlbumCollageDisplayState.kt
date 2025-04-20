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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui.state

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import uhuruphotos_android.foundation.strings.api.generated.resources.Res
import uhuruphotos_android.foundation.strings.api.generated.resources.auto_albums

data object AutoAlbumCollageDisplayState: CollageDisplayState {
    override val miniIcons: Boolean = false
    override val compactColumnsPortrait = 3
    override val compactColumnsLandscape = 5
    override val wideColumnsPortrait = 6
    override val wideColumnsLandscape = 8
    override val iconResource = null
    override val maintainAspectRatio = false
    override val allowsAnimatedVideoThumbnails = true
    override val friendlyName = Res.string.auto_albums
    override val zoomIn = this
    override val zoomOut = this
    override val allowsPinchGestures = false
    override val usingStaggeredGrid = true
}