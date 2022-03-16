package com.savvasdalkitsis.librephotos.main.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.savvasdalkitsis.librephotos.main.state.MainState
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun MainView(
    contentPadding: PaddingValues,
    state: MainState,
) {
    LazyStaggeredGrid(
        contentPadding = contentPadding,
//        contentPadding = WindowInsets
//            .systemBars
//            .only(Top + Bottom)
//            .asPaddingValues(),
        modifier = Modifier.padding(
            start = 1.dp,
            end = 1.dp,
        ),
        cells = StaggeredCells.Adaptive(minSize = 180.dp)) {
        state.images.forEach { image ->
            item {
                CoilImage(
                    modifier = Modifier.padding(1.dp),
                    imageModel = image,
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
}