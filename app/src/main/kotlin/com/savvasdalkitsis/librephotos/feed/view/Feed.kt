package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.extensions.toColor
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState

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
        LazyStaggeredGrid(
            contentPadding = contentPadding,
            modifier = Modifier.padding(
                start = 1.dp,
                end = 1.dp,
            ),
            //        columns = GridCells.Adaptive(minSize = 180.dp)
            cells = StaggeredCells.Fixed(2)
        ) {
            state.albums.forEach { album ->
                //            item {
                //                Column(modifier = Modifier.padding(2.dp)) {
                //                    Text(
                //                        text = album.date.ifEmpty { "Album" },
                //                        fontSize = 24.sp,
                //                        fontWeight = FontWeight.Bold
                //                    )
                //                    val location = album.location.orEmpty()
                //                    if (location.isNotEmpty()) {
                //                        Text(
                //                            text = location,
                //                            fontSize = 18.sp,
                //                            fontWeight = FontWeight.Light
                //                        )
                //                    }
                //                }
                //            }
                album.photos.forEach { photo ->
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
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photo.url)
                                    .crossfade(true)
                                    .build(),
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