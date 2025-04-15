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
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.big
import uhuruphotos_android.foundation.strings.api.generated.resources.comfortable
import uhuruphotos_android.foundation.strings.api.generated.resources.compact
import uhuruphotos_android.foundation.strings.api.generated.resources.full
import uhuruphotos_android.foundation.strings.api.generated.resources.tiny
import uhuruphotos_android.foundation.strings.api.generated.resources.yearly
import kotlin.math.max
import kotlin.math.min

enum class PredefinedCollageDisplayState(
    override val miniIcons: Boolean,
    override val compactColumnsPortrait: Int,
    override val compactColumnsLandscape: Int,
    override val wideColumnsPortrait: Int,
    override val wideColumnsLandscape: Int,
    @DrawableRes override val iconResource: Int,
    override val maintainAspectRatio: Boolean,
    override val allowsAnimatedVideoThumbnails: Boolean = true,
    override val friendlyName: StringResource,
    override val usingStaggeredGrid: Boolean = true,
) : CollageDisplayState {
    YEARLY(
        miniIcons = true,
        compactColumnsPortrait = 10,
        compactColumnsLandscape = 12,
        wideColumnsPortrait = 16,
        wideColumnsLandscape = 18,
        iconResource = drawable.ic_month,
        maintainAspectRatio = false,
        allowsAnimatedVideoThumbnails = false,
        friendlyName = string.yearly,
    ),
    TINY(
        miniIcons = false,
        compactColumnsPortrait = 5,
        compactColumnsLandscape = 8,
        wideColumnsPortrait = 8,
        wideColumnsLandscape = 9,
        iconResource = drawable.ic_collage_tiny,
        maintainAspectRatio = false,
        friendlyName = string.tiny,
    ),
    COMPACT(
        miniIcons = false,
        compactColumnsPortrait = 4,
        compactColumnsLandscape = 7,
        wideColumnsPortrait = 7,
        wideColumnsLandscape = 8,
        iconResource = drawable.ic_collage_compact,
        maintainAspectRatio = true,
        friendlyName = string.compact,
    ),
    COMFORTABLE(
        miniIcons = false,
        compactColumnsPortrait = 3,
        compactColumnsLandscape = 4,
        wideColumnsPortrait = 4,
        wideColumnsLandscape = 6,
        iconResource = drawable.ic_collage_comfortable,
        maintainAspectRatio = true,
        friendlyName = string.comfortable,
    ),
    BIG(
        miniIcons = false,
        compactColumnsPortrait = 2,
        compactColumnsLandscape = 3,
        wideColumnsPortrait = 3,
        wideColumnsLandscape = 5,
        iconResource = drawable.ic_collage_big,
        maintainAspectRatio = true,
        friendlyName = string.big,
    ),
    FULL(
        miniIcons = false,
        compactColumnsPortrait = 1,
        compactColumnsLandscape = 2,
        wideColumnsPortrait = 2,
        wideColumnsLandscape = 4,
        iconResource = drawable.ic_collage_full,
        maintainAspectRatio = true,
        friendlyName = string.full,
    );

    override val zoomIn: CollageDisplayState get() = entries[min(ordinal + 1, entries.size - 1)]
    override val zoomOut: CollageDisplayState get() = entries[max(0, ordinal -1)]
    override val allowsPinchGestures = true

    companion object {
        val default = COMFORTABLE
    }
}