package com.savvasdalkitsis.uhuruphotos.feedpage.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.feedpage.view.FeedPage
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel.FeedPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel.FeedPageViewModel
import com.savvasdalkitsis.uhuruphotos.home.module.HomeModule.HomeNavigationTargetSearch
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import javax.inject.Inject

class FeedPageNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val feedPageEffectsHandler: FeedPageEffectsHandler,
    private val settingsUseCase: SettingsUseCase,
    @HomeNavigationTargetSearch private val searchNavigationName: String,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() {
        navigationTarget<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageViewModel>(
            name = name,
            effects = feedPageEffectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(FeedPageAction.LoadFeed) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            FeedPage(controllersProvider, state, name, searchNavigationName, actions)
        }
    }

    companion object {
        const val name = "feed"
    }
}
