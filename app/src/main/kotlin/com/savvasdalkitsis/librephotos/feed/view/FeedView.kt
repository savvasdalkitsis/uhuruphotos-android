package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.savvasdalkitsis.librephotos.extensions.toColor
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.LocalCoilImageLoader

@Composable
fun FeedView(
    contentPadding: PaddingValues,
    state: FeedState,
    imageLoader: ImageLoader? = null,
) {
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
                        CoilImage(
                            modifier = Modifier.fillMaxWidth(),
                            imageLoader = { imageLoader ?: LocalCoilImageLoader.current!! },
                            imageModel = photo.url,
                            contentScale = ContentScale.FillBounds,
                        )
                    }
                }
            }
        }
    }
}