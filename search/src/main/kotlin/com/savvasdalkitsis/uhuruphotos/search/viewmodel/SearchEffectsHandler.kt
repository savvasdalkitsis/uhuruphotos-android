package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.savvasdalkitsis.uhuruphotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.*
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import javax.inject.Inject

class SearchEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val toaster: Toaster,
) : (SearchEffect) -> Unit {

    override fun invoke(
        effect: SearchEffect,
    ) = when (effect) {
        HideKeyboard -> controllersProvider.keyboardController!!.hide()
        FocusSearchBar -> controllersProvider.focusRequester!!.requestFocus()
        ReloadApp -> {
            with(controllersProvider.navController!!) {
                backQueue.clear()
                navigate(HomeNavigationTarget.name)
            }
        }
        NavigateToEditServer -> navigateTo(
            ServerNavigationTarget.name(auto = false)
        )
        NavigateToSettings -> navigateTo(SettingsNavigationTarget.name)
        is OpenPhotoDetails -> navigateTo(
            PhotoNavigationTarget.name(effect.id, effect.center, effect.scale, effect.isVideo)
        )
        ErrorSearching -> toaster.show("There was an error while searching")
    }

    private fun navigateTo(target: String) {
        controllersProvider.navController!!.navigate(target)
    }
}
