package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.feed.view.FeedView
import com.savvasdalkitsis.librephotos.home.state.HomeState
import com.savvasdalkitsis.librephotos.main.MainScaffolding
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider

@Composable
fun Home(
    state: HomeState,
    imageLoader: ImageLoader,
    controllersProvider: ControllersProvider,
) {
    MainScaffolding(controllersProvider.navController!!) { contentPadding ->
        if (state.isLoading && state.feedState.albums.isEmpty()) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            Box {
                FeedView(contentPadding, state.feedState, imageLoader)
            }
        }
    }
}

