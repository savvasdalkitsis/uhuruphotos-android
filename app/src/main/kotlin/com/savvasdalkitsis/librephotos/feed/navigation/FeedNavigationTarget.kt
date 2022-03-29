package com.savvasdalkitsis.librephotos.feed.navigation

import androidx.navigation.NavGraphBuilder
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.view.Feed
import com.savvasdalkitsis.librephotos.feed.view.FeedPage
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.feed.viewmodel.FeedViewModel
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import javax.inject.Inject

class FeedNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val imageLoader: ImageLoader,
) {

    fun NavGraphBuilder.create() =
        navigationTarget<FeedState, FeedAction, FeedEffect, FeedViewModel>(
            name = name,
            effects = { _, _ ->  },
            viewBuilder = { state, _, _ -> FeedPage(controllersProvider, state, imageLoader) },
            controllersProvider = controllersProvider,
        )

    companion object {
        const val name = "feed"
    }
}
