package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.runtime.Composable
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider

@Composable
fun FeedPage(
    controllersProvider: ControllersProvider,
    state: FeedState,
    imageLoader: ImageLoader? = null,
) {
    HomeScaffold(controllersProvider.navController!!) { contentPadding ->
        Feed(contentPadding, state, imageLoader)
    }
}