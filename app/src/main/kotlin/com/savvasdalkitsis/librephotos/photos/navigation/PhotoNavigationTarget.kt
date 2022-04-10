package com.savvasdalkitsis.librephotos.photos.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.LoadPhoto
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.librephotos.photos.view.Photo
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.photos.viewmodel.PhotoEffectsHandler
import com.savvasdalkitsis.librephotos.photos.viewmodel.PhotoViewModel
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

class PhotoNavigationTarget @Inject constructor(
    private val effectsHandler: PhotoEffectsHandler,
) {

    @ExperimentalFoundationApi
    @FlowPreview
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    fun NavGraphBuilder.create() {
        navigationTarget<PhotoState, PhotoEffect, PhotoAction, PhotoViewModel>(
            name = name,
            effects = effectsHandler,
            enterTransition = { scaleIn() + fadeIn() },
            exitTransition = { scaleOut() + fadeOut() },
            initializer = { navBackStackEntry, actions ->
                actions(LoadPhoto(navBackStackEntry.photoId))
            }
        ) { state, actions ->
            Photo(state, actions)
        }
    }

    companion object {
        private const val name = "photo/{id}"
        fun id(id: String) = name.replace("{id}", id)
        private val NavBackStackEntry.photoId : String get() {
            return arguments!!.getString("id")!!
        }
    }
}