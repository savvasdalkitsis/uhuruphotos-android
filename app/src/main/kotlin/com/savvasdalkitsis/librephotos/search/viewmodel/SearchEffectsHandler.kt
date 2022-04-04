package com.savvasdalkitsis.librephotos.search.viewmodel

import androidx.compose.ui.ExperimentalComposeUiApi
import com.savvasdalkitsis.librephotos.feed.navigation.FeedNavigationTarget
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import javax.inject.Inject

@ExperimentalComposeUiApi
class SearchEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : (SearchEffect) -> Unit {

    override fun invoke(
        effect: SearchEffect,
    ) = when (effect) {
        SearchEffect.HideKeyboard -> controllersProvider.keyboardController!!.hide()
        SearchEffect.FocusSearchBar -> controllersProvider.focusRequester!!.requestFocus()
        SearchEffect.ReloadApp -> {
            with(controllersProvider.navController!!) {
                backQueue.clear()
                navigate(HomeNavigationTarget.name)
            }
        }
    }
}
