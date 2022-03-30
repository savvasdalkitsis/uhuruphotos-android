package com.savvasdalkitsis.librephotos.feed.view

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.extensions.toColor
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.ui.view.LazyStaggeredGrid

@Composable
fun Feed(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: FeedState,
) {
    if (state.isLoading && state.albums.isEmpty()) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        Box(modifier = Modifier
            .padding(start = 1.dp, end = 1.dp,)
        ) {
            LazyStaggeredGrid(
                columnCount = when (LocalConfiguration.current.orientation) {
                    ORIENTATION_LANDSCAPE -> 5
                    else -> 2
                },
                contentPadding = contentPadding,
            ) {
                state.albums.flatMap { it.photos }.forEach { photo ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(photo.ratio)
                                .padding(1.dp)
                                .background(photo.fallbackColor.toColor())
                        ) {
                            AsyncImage(
                                modifier = Modifier.fillMaxWidth(),
                                model = photo.url,
                                contentScale = ContentScale.FillBounds,
                                contentDescription = "photo",
                            )
                            if (photo.isVideo) {
                                Icon(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.Center),
                                    painter = painterResource(id = R.drawable.ic_play_filled),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}