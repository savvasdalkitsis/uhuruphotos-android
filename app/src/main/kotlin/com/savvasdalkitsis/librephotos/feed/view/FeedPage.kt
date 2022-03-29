package com.savvasdalkitsis.librephotos.feed.view

import androidx.compose.runtime.Composable
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider

@Composable
fun FeedPage(
    controllersProvider: ControllersProvider,
    state: FeedPageState,
    imageLoader: ImageLoader? = null,
) {
    HomeScaffold(
        controllersProvider.navController!!, userBadgeState = state.userBadgeState, imageLoader)
        { contentPadding ->
            Feed(contentPadding, state.feedState, imageLoader)
        }
}