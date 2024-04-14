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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
interface CollageDisplay {
    val miniIcons: Boolean
    val compactColumnsPortrait: Int
    val compactColumnsLandscape: Int
    val wideColumnsPortrait: Int
    val wideColumnsLandscape: Int
    @get:DrawableRes val iconResource: Int
    val maintainAspectRatio: Boolean
    val allowsAnimatedVideoThumbnails: Boolean
    @get:StringRes val friendlyName: Int
    val zoomIn: CollageDisplay
    val zoomOut: CollageDisplay
    val allowsPinchGestures: Boolean
    val usingStaggeredGrid: Boolean
}