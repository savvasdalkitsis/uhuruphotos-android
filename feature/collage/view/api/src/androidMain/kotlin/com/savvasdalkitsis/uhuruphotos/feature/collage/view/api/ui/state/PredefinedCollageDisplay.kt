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


import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import kotlin.math.max
import kotlin.math.min

enum class PredefinedCollageDisplay(
    override val miniIcons: Boolean,
    override val compactColumnsPortrait: Int,
    override val compactColumnsLandscape: Int,
    override val wideColumnsPortrait: Int,
    override val wideColumnsLandscape: Int,
    override val iconResource: ImageResource,
    override val maintainAspectRatio: Boolean,
    override val allowsAnimatedVideoThumbnails: Boolean = true,
    override val friendlyName: StringResource,
    override val usingStaggeredGrid: Boolean = true,
) : CollageDisplay {
    YEARLY(
        miniIcons = true,
        compactColumnsPortrait = 10,
        compactColumnsLandscape = 12,
        wideColumnsPortrait = 16,
        wideColumnsLandscape = 18,
        iconResource = images.ic_month,
        maintainAspectRatio = false,
        allowsAnimatedVideoThumbnails = false,
        friendlyName = strings.yearly,
    ),
    TINY(
        miniIcons = false,
        compactColumnsPortrait = 5,
        compactColumnsLandscape = 8,
        wideColumnsPortrait = 8,
        wideColumnsLandscape = 9,
        iconResource = images.ic_collage_tiny,
        maintainAspectRatio = false,
        friendlyName = strings.tiny,
    ),
    COMPACT(
        miniIcons = false,
        compactColumnsPortrait = 4,
        compactColumnsLandscape = 7,
        wideColumnsPortrait = 7,
        wideColumnsLandscape = 8,
        iconResource = images.ic_collage_compact,
        maintainAspectRatio = true,
        friendlyName = strings.compact,
    ),
    COMFORTABLE(
        miniIcons = false,
        compactColumnsPortrait = 3,
        compactColumnsLandscape = 4,
        wideColumnsPortrait = 4,
        wideColumnsLandscape = 6,
        iconResource = images.ic_collage_comfortable,
        maintainAspectRatio = true,
        friendlyName = strings.comfortable,
    ),
    BIG(
        miniIcons = false,
        compactColumnsPortrait = 2,
        compactColumnsLandscape = 3,
        wideColumnsPortrait = 3,
        wideColumnsLandscape = 5,
        iconResource = images.ic_collage_big,
        maintainAspectRatio = true,
        friendlyName = strings.big,
    ),
    FULL(
        miniIcons = false,
        compactColumnsPortrait = 1,
        compactColumnsLandscape = 2,
        wideColumnsPortrait = 2,
        wideColumnsLandscape = 4,
        iconResource = images.ic_collage_full,
        maintainAspectRatio = true,
        friendlyName = strings.full,
    );

    override val zoomIn: CollageDisplay get() = entries[min(ordinal + 1, entries.size - 1)]
    override val zoomOut: CollageDisplay get() = entries[max(0, ordinal -1)]
    override val allowsPinchGestures = true

    companion object {
        val default = COMFORTABLE
    }
}