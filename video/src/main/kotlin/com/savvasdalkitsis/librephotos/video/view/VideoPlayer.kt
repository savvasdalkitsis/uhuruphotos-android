package com.savvasdalkitsis.librephotos.video.view

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.savvasdalkitsis.librephotos.image.view.Image
import com.savvasdalkitsis.librephotos.video.LocalExoPlayer
import com.savvasdalkitsis.librephotos.video.R

@Composable
fun Video(
    modifier: Modifier,
    videoUrl: String,
    play: Boolean,
    videoThumbnailUrl: String,
) {
    val exoPlayer = LocalExoPlayer.current!!
    val context = LocalContext.current
    var showPlayer by remember { mutableStateOf(false) }

    val playerView = remember {
        @SuppressLint("InflateParams")
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as StyledPlayerView
        playerView.apply {
            player = exoPlayer
        }
    }


    if (showPlayer) {
        AndroidView(modifier = modifier, factory = { playerView })
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                url = videoThumbnailUrl,
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    }

    LaunchedEffect(videoUrl, play) {
        if (!play) {
            exoPlayer.pause()
        } else {
            exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == STATE_READY) {
                        showPlayer = true
                    }
                }
            })
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.pause()
        }
    }
}