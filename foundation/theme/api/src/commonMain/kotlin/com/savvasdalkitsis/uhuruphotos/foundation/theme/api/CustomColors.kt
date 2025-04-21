package com.savvasdalkitsis.uhuruphotos.foundation.theme.api

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
data object CustomColors {
    val syncError = Color(158, 6, 37)
    val syncSuccess = Color(21, 158, 6, 255)
    val syncQueued = Color(33, 150, 243, 255)
    val selected = Color(69, 158, 59, 255)
    val alert = Color(255, 152, 0, 255)
    val selectedBackground = selected.copy(alpha = 0.2f)
    val emptyItem: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
}