package com.savvasdalkitsis.librephotos.search.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.home.module.HomeModule.HomeNavigationTargetFeed
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.view.SearchPage
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.search.viewmodel.SearchEffectsHandler
import com.savvasdalkitsis.librephotos.search.viewmodel.SearchViewModel
import javax.inject.Inject

class SearchNavigationTarget @Inject constructor(
    private val effectsHandler: SearchEffectsHandler,
    private val controllersProvider: ControllersProvider,
    @HomeNavigationTargetFeed private val feedNavigationName: String,
) {

    fun NavGraphBuilder.create() {
        navigationTarget<SearchState, SearchEffect, SearchAction, SearchViewModel>(
            name = name,
            effects = effectsHandler,
            initializer = { _, actions -> actions(SearchAction.Initialise) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            SearchPage(state, actions, feedNavigationName, name, controllersProvider)
        }
    }

    companion object {
        const val name = "search"
    }
}