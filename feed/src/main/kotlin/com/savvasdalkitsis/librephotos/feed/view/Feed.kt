package com.savvasdalkitsis.librephotos.feed.view

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar
import com.savvasdalkitsis.librephotos.ui.window.WindowSize
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

private const val zoomMin = 0.8f
private const val zoomMax = 1.2f
private const val zoomLowTrigger = 0.9f
private const val zoomHighTrigger = 1.1f

@Composable
fun Feed(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: FeedState,
    gridState: LazyGridState = rememberLazyGridState(),
    listState: LazyListState = rememberLazyListState(),
    onPhotoSelected: PhotoSelected = { _, _, _ ->},
    onChangeDisplay: (FeedDisplay) -> Unit = {},
) {
    val zoom = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    if (state.isLoading && state.albums.isEmpty()) {
        FullProgressBar()
    } else {
        val feedDisplay = state.feedDisplay
        val modifier = Modifier
            .pointerInput(feedDisplay) {
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
                                        zoom.snapTo(
                                            max(
                                                zoomMin,
                                                min(zoom.value * zoomChange, zoomMax)
                                            )
                                        )
                                    }
                                }

                                event.changes.fastForEach {
                                    if (it.positionChanged()) {
                                        it.consumeAllChanges()
                                    }
                                }
                            }
                        } while (!canceled && event.changes.fastAny { it.pressed })
                        if (zoom.value < zoomLowTrigger) {
                            onChangeDisplay(feedDisplay.zoomOut)
                        }
                        if (zoom.value > zoomHighTrigger) {
                            onChangeDisplay(feedDisplay.zoomIn)
                        }
                        coroutineScope.launch {
                            zoom.animateTo(1f)
                        }
                    }
                }
            }
            .scale(zoom.value)
        val columnCount = feedDisplay.columnCount(
            windowSizeClass = WindowSize.LOCAL_WIDTH.current,
            landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
        if (feedDisplay == FeedDisplay.TINY) {
            GridDateFeed(
                modifier = modifier,
                contentPadding = contentPadding,
                albums = state.albums,
                columnCount = columnCount,
                gridState = gridState,
                onPhotoSelected = onPhotoSelected
            )
        } else {
            StaggeredDateFeed(
                modifier = modifier,
                contentPadding = contentPadding,
                albums = state.albums,
                listState = listState,
                columnCount = columnCount,
                shouldAddEmptyPhotosInRows = feedDisplay.shouldAddEmptyPhotosInRows,
                onPhotoSelected = onPhotoSelected
            )
        }
    }
}