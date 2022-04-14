package com.savvasdalkitsis.librephotos.feed.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feed.view.FeedPage
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.feed.viewmodel.FeedPageEffectsHandler
import com.savvasdalkitsis.librephotos.feed.viewmodel.FeedPageViewModel
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

class FeedPageNavigationTarget @Inject constructor(
    private val controllersProvider: com.savvasdalkitsis.librephotos.navigation.ControllersProvider,
    private val feedPageEffectsHandler: FeedPageEffectsHandler,
) {

    @FlowPreview
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    fun NavGraphBuilder.create() {
        navigationTarget<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageViewModel>(
            name = name,
            effects = feedPageEffectsHandler,
            initializer = { _, actions -> actions(FeedPageAction.LoadFeed) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            FeedPage(controllersProvider, state, actions)
        }
    }

    companion object {
        const val name = "feed"
    }
}
