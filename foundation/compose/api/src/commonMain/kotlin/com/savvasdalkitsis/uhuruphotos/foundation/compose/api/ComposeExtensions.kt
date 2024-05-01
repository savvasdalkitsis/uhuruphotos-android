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
package com.savvasdalkitsis.uhuruphotos.foundation.compose.api

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log

@Composable fun String?.toColor(): Color = when {
    isNullOrBlank() -> MaterialTheme.colors.background
    else -> try {
        Color.parseColor(this)
    } catch (e: Exception) {
        log(e)
        MaterialTheme.colors.background
    }
}

fun Color.Companion.parseColor(colorString: String): Color {
    if (colorString.getOrNull(0) == '#') {
        // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            // Set the alpha value
            color = color or 0x00000000ff000000
        } else if (colorString.length != 9) {
            throw IllegalArgumentException("Unknown color");
        }
        return Color(color.toInt())
    }
    throw IllegalArgumentException("Unknown color");
}

@Composable
fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    start: Dp = this.calculateStartPadding(layoutDirection),
    top: Dp = this.calculateTopPadding(),
    end: Dp = this.calculateEndPadding(layoutDirection),
    bottom: Dp = this.calculateBottomPadding(),
): PaddingValues = PaddingValues(start, top, end, bottom)

fun Modifier.blurIf(condition: Boolean): Modifier = composed {
    val blur: Float by animateFloatAsState(if (condition) 8f else 0f, label = "blurAnimation")
    this.blur(blur.dp)
}