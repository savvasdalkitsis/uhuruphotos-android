package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme

import androidx.compose.runtime.compositionLocalOf

val LocalTheme =
    compositionLocalOf<Theme> { throw IllegalStateException("not initialized") }

