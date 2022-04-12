package com.savvasdalkitsis.librephotos.photos.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.hilt.navigation.compose.hiltViewModel
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
            enterTransition = {
                val offset = targetState.offset
                scaleIn(transformOrigin = TransformOrigin(offset.x, offset.y))
            },
            exitTransition = {
                val offset = initialState.offset
                scaleOut(transformOrigin = TransformOrigin(offset.x, offset.y))
            },
            initializer = { navBackStackEntry, actions ->
                actions(LoadPhoto(navBackStackEntry.photoId))
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Photo(state, actions)
        }
    }

    companion object {
        private const val name = "photo/{id}/{x}/{y}"
        fun id(id: String) = idWithOffset(id, Offset(0.5f, 0.5f))
        fun idWithOffset(id: String, offset: Offset) = name
            .replace("{id}", id)
            .replace("{x}", offset.x.toString())
            .replace("{y}", offset.y.toString())

        private val NavBackStackEntry.photoId : String get() = get("id")
        private val NavBackStackEntry.offset : Offset get() =
            Offset(get("x").toFloat(), get("y").toFloat())

        private fun NavBackStackEntry.get(arg: String) = arguments!!.getString(arg)!!
    }
}