package com.savvasdalkitsis.uhuruphotos.foundation.image.api

import androidx.compose.runtime.compositionLocalOf

val LocalAnimatedVideoThumbnails =
    compositionLocalOf<Boolean> { throw IllegalStateException("not initialized") }