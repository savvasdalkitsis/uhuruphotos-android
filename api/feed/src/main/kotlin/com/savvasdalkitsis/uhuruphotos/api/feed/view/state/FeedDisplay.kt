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
package com.savvasdalkitsis.uhuruphotos.api.feed.view.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface FeedDisplay {
    val miniIcons: Boolean
    val compactColumnsPortrait: Int
    val compactColumnsLandscape: Int
    val wideColumnsPortrait: Int
    val wideColumnsLandscape: Int
    val shouldAddEmptyPhotosInRows: Boolean
    @get:DrawableRes val iconResource: Int
    val maintainAspectRatio: Boolean
    @get:StringRes val friendlyName: Int
    val zoomIn: FeedDisplay
    val zoomOut: FeedDisplay
    val allowsPinchGestures: Boolean
}