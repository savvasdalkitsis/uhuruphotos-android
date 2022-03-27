package com.savvasdalkitsis.librephotos.search.viewmodel

import androidx.compose.ui.ExperimentalComposeUiApi
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect

@ExperimentalComposeUiApi
class SearchEffectsHandler : (SearchEffect, ControllersProvider) -> Unit {

    override fun invoke(
        effect: SearchEffect,
        controllersProvider: ControllersProvider,
    ) = when (effect) {
        SearchEffect.HideKeyboard -> controllersProvider.keyboardController!!.hide()
        SearchEffect.FocusSearchBar -> controllersProvider.focusRequester!!.requestFocus()
    }
}
