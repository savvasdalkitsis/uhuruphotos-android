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

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.ThumbnailImage
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.R

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
    val context = LocalContext.current
    var showPlayer by remember { mutableStateOf(false) }

    val playerView = remember(showControls, mute, crop) {
        @SuppressLint("InflateParams")
        val layout = LayoutInflater.from(context).inflate(
            R.layout.video_player,
            null,
            false
        )
        val playerView =
            layout.findViewById(R.id.playerView) as StyledPlayerView
        playerView.apply {
            player = exoPlayer
            if (mute) {
                exoPlayer.volume = 0f
            }
            if (crop) {
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
            playerView.useController = showControls
        }
    }


    if (showPlayer) {
        AndroidView(modifier = modifier, factory = { playerView })
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            ThumbnailImage(
                modifier = Modifier.fillMaxSize(),
                url = videoThumbnailUrl,
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
            if (showProgress) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
    }

    LaunchedEffect(videoUrl, play) {
        if (!play) {
            exoPlayer.pause()
        } else {
            exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        showPlayer = true
                    }
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    if (!isLoading) {
                        onFinishedLoading()
                    }
                }
            })
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }
}