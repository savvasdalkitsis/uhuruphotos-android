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
package com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.MaterialTheme
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
    isNullOrBlank() -> MaterialTheme.colorScheme.background
    else -> try {
        Color(parseColor(this))
    } catch (e: Exception) {
        log(e)
        MaterialTheme.colorScheme.background
    }
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

private val sColorNameMap = mapOf<String, Int>(
    "black" to 0x000000,
    "darkgray" to 0x444444,
    "gray" to 0x888888,
    "lightgray" to 0xCCCCCC,
    "white" to 0xFFFFFF,
    "red" to 0xFF0000,
    "green" to 0x00FF00,
    "blue" to 0x0000FF,
    "yellow" to 0xFFFF00,
    "cyan" to 0x00FFFF,
    "magenta" to 0xFF00FF,
    "aqua" to 0x00FFFF,
    "fuchsia" to 0xFF00FF,
    "darkgrey" to 0x444444,
    "grey" to 0x888888,
    "lightgrey" to 0xCCCCCC,
    "lime" to 0x00FF00,
    "maroon" to 0x800000,
    "navy" to 0x000080,
    "olive" to 0x808000,
    "purple" to 0x800080,
    "silver" to 0xC0C0C0,
    "teal" to 0x008080,
)

fun parseColor(colorString: String): Int {
    if (colorString[0] == '#') {
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            color = color or 0x00000000ff000000L
        } else require(colorString.length == 9) { "Unknown color" }
        return color.toInt()
    } else {
        val color: Int? = sColorNameMap[colorString.lowercase()]
        if (color != null) {
            return color
        }
    }
    throw IllegalArgumentException("Unknown color")
}