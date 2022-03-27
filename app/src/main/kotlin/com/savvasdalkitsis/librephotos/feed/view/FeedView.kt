package com.savvasdalkitsis.librephotos.feed.view

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.savvasdalkitsis.librephotos.extensions.toAndroidColor
import com.savvasdalkitsis.librephotos.extensions.toColor
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import com.skydoves.landscapist.rememberDrawablePainter

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
        cells = StaggeredCells.Adaptive(minSize = 180.dp)
    ) {
        state.photos.forEach { photo ->
            item {
                Box(modifier = Modifier
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