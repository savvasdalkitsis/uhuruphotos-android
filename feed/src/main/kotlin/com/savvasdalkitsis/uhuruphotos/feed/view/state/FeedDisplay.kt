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
package com.savvasdalkitsis.uhuruphotos.feed.view.state

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact

interface FeedDisplay {
    val compactColumnsPortrait: Int
    val compactColumnsLandscape: Int
    val wideColumnsPortrait: Int
    val wideColumnsLandscape: Int
    val shouldAddEmptyPhotosInRows: Boolean
    val iconResource: Int
    val maintainAspectRatio: Boolean
    val friendlyName: String
    val zoomIn: FeedDisplay
    val zoomOut: FeedDisplay

    fun columnCount(
        widthSizeClass: WindowWidthSizeClass,
        landscape: Boolean,
    ) = when {
        landscape -> when (widthSizeClass) {
            Compact -> compactColumnsLandscape
            else -> wideColumnsLandscape
        }
        else -> when (widthSizeClass) {
            Compact -> compactColumnsPortrait
            else -> wideColumnsPortrait
        }
    }
}