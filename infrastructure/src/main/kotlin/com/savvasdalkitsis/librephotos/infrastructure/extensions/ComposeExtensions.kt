package com.savvasdalkitsis.librephotos.infrastructure.extensions

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable fun String?.toColor(): Color = when (this) {
    null -> MaterialTheme.colors.background
    else -> try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        MaterialTheme.colors.background
    }
}

fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    start: Dp = this.calculateStartPadding(layoutDirection),
    top: Dp = this.calculateTopPadding(),
    end: Dp = this.calculateEndPadding(layoutDirection),
    bottom: Dp = this.calculateBottomPadding(),
): PaddingValues = PaddingValues(start, top, end, bottom)

fun Modifier.blurIf(condition: Boolean): Modifier = composed {
    val blur: Float by animateFloatAsState(if (condition) 8f else 0f)
    blur(blur.dp)
}