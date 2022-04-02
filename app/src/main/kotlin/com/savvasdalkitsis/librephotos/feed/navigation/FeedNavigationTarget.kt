package com.savvasdalkitsis.librephotos.feed.navigation

import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.view.FeedPage
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.feed.viewmodel.FeedViewModel
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import com.savvasdalkitsis.librephotos.viewmodel.noOp
import javax.inject.Inject

class FeedNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
) {

    fun NavGraphBuilder.create() {
        navigationTarget<FeedPageState, FeedEffect, FeedAction, FeedViewModel>(
            name = name,
            effects = noOp(),
            initializer = { _, actions -> actions(FeedAction.LoadFeed) }
        ) { state, _ ->
            FeedPage(controllersProvider, state)
        }
    }

    companion object {
        const val name = "feed"
    }
}
