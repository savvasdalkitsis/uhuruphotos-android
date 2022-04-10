package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.savvasdalkitsis.librephotos.main.view.MainScaffold
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.NavigateBack
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.zoom.zoomable

@ExperimentalFoundationApi
@Composable
fun Photo(
    state: PhotoState,
    actions: (PhotoAction) -> Unit,
) {
    MainScaffold(
        title = {},
        toolbarColor = Color.Transparent,
        displayed = state.isUIShowing,
        navigationIcon = {
            IconButton(onClick = { actions(NavigateBack) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            }
        })
    {
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            var showLowRes = remember { true }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .zoomable(onTap = {
                        actions(PhotoAction.ToggleUI)
                    })
            ) {
                if (showLowRes) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.lowResUrl)
                            .build(),
                        contentScale = ContentScale.Fit,
                        contentDescription = "",
                    )
                }
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.fullResUrl)
                        .size(Size.ORIGINAL)
                        .listener(onSuccess = { _, _ ->
                            showLowRes = false
                        })
                        .build(),
                    contentScale = ContentScale.Fit,
                    contentDescription = "photo",
                )
            }
        }
    }
}