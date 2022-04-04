package com.savvasdalkitsis.librephotos.search.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.view.SearchPage
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.search.viewmodel.SearchEffectsHandler
import com.savvasdalkitsis.librephotos.search.viewmodel.SearchViewModel
import javax.inject.Inject

class SearchNavigationTarget @ExperimentalComposeUiApi
@Inject constructor(
    private val effectsHandler: SearchEffectsHandler,
    private val controllersProvider: ControllersProvider,
) {

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    fun NavGraphBuilder.create() {
        navigationTarget<SearchState, SearchEffect, SearchAction, SearchViewModel>(
            name = name,
            effects = effectsHandler,
            initializer = { _, actions -> actions(SearchAction.Initialise) },
        ) { state, actions ->
            SearchPage(state, actions, controllersProvider)
        }
    }

    companion object {
        const val name = "search"
    }
}