package com.savvasdalkitsis.uhuruphotos.video

import androidx.compose.runtime.compositionLocalOf
import com.google.android.exoplayer2.ExoPlayer

val LocalExoPlayer = compositionLocalOf<ExoPlayer?> { throw IllegalStateException("not initialized") }