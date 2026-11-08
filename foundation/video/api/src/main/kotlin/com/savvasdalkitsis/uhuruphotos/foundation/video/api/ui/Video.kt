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
package com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider

@Composable
fun Video(
    modifier: Modifier,
    videoUrl: String,
    play: Boolean,
    videoThumbnailUrl: String,
    repeatMode: Int = Player.REPEAT_MODE_OFF,
    showControls: Boolean = true,
    showProgress: Boolean = true,
    mute: Boolean = false,
    crop: Boolean = false,
    onFinishedLoading: () -> Unit,
) {
    val exoPlayer = LocalExoPlayerProvider.current.createExoplayer(videoUrl)
    Video(
        modifier = modifier,
        exoPlayer = exoPlayer,
        videoUrl = videoUrl,
        play = play,
        videoThumbnailUrl = videoThumbnailUrl,
        repeatMode = repeatMode,
        showControls = showControls,
        showProgress = showProgress,
        mute = mute,
        crop = crop,
        onFinishedLoading = onFinishedLoading
    )
}

@NonSkippableComposable
@Composable
fun Video(
    modifier: Modifier,
    exoPlayer: ExoPlayer,
    videoUrl: String,
    play: Boolean,
    videoThumbnailUrl: String,
    repeatMode: Int = Player.REPEAT_MODE_OFF,
    showControls: Boolean = true,
    showProgress: Boolean = true,
    mute: Boolean = false,
    crop: Boolean = false,
    onFinishedLoading: () -> Unit,
) {
    exoPlayer.repeatMode = repeatMode
    var showPlayer by remember { mutableStateOf(false) }
    val playerView = rememberPlayerView(exoPlayer, showControls, mute, crop)

    if (showPlayer) {
        AndroidView(modifier = modifier, factory = { playerView })
    } else {
        VideoThumbnail(modifier, videoThumbnailUrl, showProgress)
    }

    PlaybackEffect(exoPlayer, videoUrl, play, onReady = { showPlayer = true }, onFinishedLoading)
}