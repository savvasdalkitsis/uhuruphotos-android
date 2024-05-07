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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

@Immutable
interface CollageDisplay {
    val miniIcons: Boolean
    val compactColumnsPortrait: Int
    val compactColumnsLandscape: Int
    val wideColumnsPortrait: Int
    val wideColumnsLandscape: Int
    val iconResource: ImageResource?
    val maintainAspectRatio: Boolean
    val allowsAnimatedVideoThumbnails: Boolean
    val friendlyName: StringResource?
    val zoomIn: CollageDisplay
    val zoomOut: CollageDisplay
    val allowsPinchGestures: Boolean
    val usingStaggeredGrid: Boolean
}