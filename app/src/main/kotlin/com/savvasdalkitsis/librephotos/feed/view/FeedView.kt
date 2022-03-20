package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.savvasdalkitsis.librephotos.feed.state.FeedState
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import kotlin.math.min

@Composable
fun FeedView(
    contentPadding: PaddingValues,
    state: FeedState,
    imageLoader: ImageLoader? = null,
) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 180.dp)) {
        state.images.forEach { image ->
            item {
                CoilImage(
                    modifier = Modifier.padding(1.dp),
                    imageModel = image,
                    imageLoader = { imageLoader ?: LocalCoilImageLoader.current!! },
                    shimmerParams = ShimmerParams(
                        baseColor = MaterialTheme.colors.background,
                        highlightColor = MaterialTheme.colors.primary,
                        durationMillis = 350,
                        dropOff = 0.65f,
                        tilt = 20f
                    ),
                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                    contentScale = ContentScale.Fit,
                    //                    // shows a placeholder while loading the image.
                    //                    placeHolder = ImageBitmap.imageResource(R.drawable.placeholder),
                    //                    // shows an error ImageBitmap when the request failed.
                    //                    error = ImageBitmap.imageResource(R.drawable.error)
                )
            }
        }
    }
//    LazyStaggeredGrid(
//        contentPadding = contentPadding,
//        modifier = Modifier.padding(
//            start = 1.dp,
//            end = 1.dp,
//        ),
//        cells = StaggeredCells.Adaptive(minSize = 180.dp)) {
//        state.images.forEach { image ->
//            item {
//                CoilImage(
//                    modifier = Modifier.padding(1.dp),
//                    imageModel = image,
//                    shimmerParams = ShimmerParams(
//                        baseColor = MaterialTheme.colors.background,
//                        highlightColor = MaterialTheme.colors.primary,
//                        durationMillis = 350,
//                        dropOff = 0.65f,
//                        tilt = 20f
//                    ),
//                    // Crop, Fit, Inside, FillHeight, FillWidth, None
//                    contentScale = ContentScale.Fit,
//                    //                    // shows a placeholder while loading the image.
//                    //                    placeHolder = ImageBitmap.imageResource(R.drawable.placeholder),
//                    //                    // shows an error ImageBitmap when the request failed.
//                    //                    error = ImageBitmap.imageResource(R.drawable.error)
//                )
//            }
//        }
//    }
}