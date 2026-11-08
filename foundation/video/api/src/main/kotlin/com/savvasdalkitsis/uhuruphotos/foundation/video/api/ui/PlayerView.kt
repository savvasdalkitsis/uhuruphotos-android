/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.R

@Composable
internal fun rememberPlayerView(
    exoPlayer: ExoPlayer,
    showControls: Boolean,
    mute: Boolean,
    crop: Boolean,
): PlayerView {
    val context = LocalContext.current
    return remember(showControls, mute, crop) {
        playerView(context, exoPlayer, showControls, mute, crop)
    }
}

@OptIn(UnstableApi::class)
internal fun playerView(
    context: Context,
    exoPlayer: ExoPlayer,
    showControls: Boolean,
    mute: Boolean,
    crop: Boolean,
): PlayerView {
    @SuppressLint("InflateParams")
    val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
    return layout.findViewById<PlayerView>(R.id.playerView).apply {
        player = exoPlayer
        if (mute) {
            exoPlayer.volume = 0f
        }
        if (crop) {
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
        useController = showControls
    }
}
