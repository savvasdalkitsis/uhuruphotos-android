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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@Composable
internal fun PlaybackEffect(
    exoPlayer: ExoPlayer,
    videoUrl: String,
    play: Boolean,
    onReady: () -> Unit,
    onFinishedLoading: () -> Unit,
) {
    val startPosition = rememberSaveable(videoUrl, saver = playbackPositionSaver(exoPlayer)) { 0L }
    LaunchedEffect(videoUrl, play) {
        if (play) {
            exoPlayer.startOrResume(videoUrl, startPosition, VideoLoadingListener(onReady, onFinishedLoading))
        } else {
            exoPlayer.pause()
        }
    }
}

private fun ExoPlayer.startOrResume(videoUrl: String, startPosition: Long, listener: Player.Listener) {
    if (currentMediaItem?.mediaId != videoUrl) {
        setMediaItem(MediaItem.Builder().setMediaId(videoUrl).setUri(videoUrl).build(), startPosition)
        addListener(listener)
        prepare()
    }
    playWhenReady = true
}

private fun playbackPositionSaver(player: Player): Saver<Long, Long> = Saver(
    save = { player.currentPosition },
    restore = { it },
)
