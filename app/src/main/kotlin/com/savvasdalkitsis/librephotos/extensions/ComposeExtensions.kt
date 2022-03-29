package com.savvasdalkitsis.librephotos.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import timber.log.Timber
import java.lang.Exception

@Composable fun String?.toColor(): Color = when (this) {
    null -> MaterialTheme.colors.background
    else -> try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        MaterialTheme.colors.background
    }
}

@Composable fun String?.toAndroidColor(): Int = toColor().toArgb()

fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    start: Dp = this.calculateStartPadding(layoutDirection),
    top: Dp = this.calculateTopPadding(),
    end: Dp = this.calculateEndPadding(layoutDirection),
    bottom: Dp = this.calculateBottomPadding(),
): PaddingValues = PaddingValues(start, top, end, bottom)
