package com.savvasdalkitsis.librephotos.feed.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.view.FeedPage
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.feed.viewmodel.FeedEffectsHandler
import com.savvasdalkitsis.librephotos.feed.viewmodel.FeedViewModel
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import javax.inject.Inject

class FeedNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val feedEffectsHandler: FeedEffectsHandler,
) {

    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    fun NavGraphBuilder.create() {
        navigationTarget<FeedPageState, FeedEffect, FeedAction, FeedViewModel>(
            name = name,
            effects = feedEffectsHandler,
            initializer = { _, actions -> actions(FeedAction.LoadFeed) }
        ) { state, actions ->
            FeedPage(controllersProvider, state, actions)
        }
    }

    companion object {
        const val name = "feed"
    }
}
