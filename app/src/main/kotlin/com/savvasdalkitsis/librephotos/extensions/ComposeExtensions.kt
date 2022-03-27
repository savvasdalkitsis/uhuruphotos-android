package com.savvasdalkitsis.librephotos.extensions

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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