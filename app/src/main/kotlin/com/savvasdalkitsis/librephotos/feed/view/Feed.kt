package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay.BY_DATE
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay.FULL
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.photos.model.Photo
import kotlinx.coroutines.launch

@Composable
fun Feed(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: FeedState,
    onPhotoSelected: (Photo) -> Unit = {},
    onChangeDisplay: (FeedDisplay) -> Unit = {},
) {
    val zoom = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    if (state.isLoading && state.albums.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        val modifier = Modifier
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                            if (event.changes.size > 1) {
                                if (!canceled) {
                                    val zoomChange = event.calculateZoom()
                                    coroutineScope.launch {
                                        zoom.snapTo(zoom.value * zoomChange)
                                    }
                                }

                                event.changes.fastForEach {
                                    if (it.positionChanged()) {
                                        it.consumeAllChanges()
                                    }
                                }
                            }
                        } while (!canceled && event.changes.fastAny { it.pressed })
                        if (zoom.value < 0.8) {
                            onChangeDisplay(BY_DATE)
                        }
                        if (zoom.value > 1.2) {
                            onChangeDisplay(FULL)
                        }
                        coroutineScope.launch {
                            zoom.animateTo(1f)
                        }
                    }
                }
            }
        when (state.feedDisplay) {
            FULL -> FullFeed(modifier, contentPadding, state.albums, onPhotoSelected)
            BY_DATE -> ByDateFeed(modifier, contentPadding, state.albums, onPhotoSelected)
        }
    }
}