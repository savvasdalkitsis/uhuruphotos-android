/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.photos.navigation

import androidx.compose.animation.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.LoadPhoto
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.photos.view.Photo
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.photos.viewmodel.PhotoEffectsHandler
import com.savvasdalkitsis.uhuruphotos.photos.viewmodel.PhotoViewModel
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PhotoNavigationTarget @Inject constructor(
    private val effectsHandler: PhotoEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() {
        navigationTarget<PhotoState, PhotoEffect, PhotoAction, PhotoViewModel>(
            name = name,
            effects = effectsHandler,
            themeMode = MutableStateFlow(ThemeMode.DARK_MODE),
            enterTransition = {
                slideIn(initialOffset = { fullSize ->
                    targetState.center.offsetFrom(fullSize)
                }) +
                        scaleIn(initialScale = targetState.scale) + fadeIn()
            },
            exitTransition = {
                slideOut(targetOffset = { fullSize ->
                    initialState.center.offsetFrom(fullSize)
                }) +
                        scaleOut(targetScale = initialState.scale) + fadeOut()
            },
            initializer = { navBackStackEntry, actions ->
                actions(LoadPhoto(navBackStackEntry.photoId, navBackStackEntry.isVideo))
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Photo(state, actions)
        }
    }

    companion object {
        private const val name = "details/{type}/{id}/{centerX}/{centerY}/{scale}"
        fun name(id: String, offset: Offset, scale: Float, isVideo: Boolean) = name
            .replace("{id}", id)
            .replace("{centerX}", offset.x.toString())
            .replace("{centerY}", offset.y.toString())
            .replace("{scale}", scale.toString())
            .replace(
                "{type}", when {
                    isVideo -> "video"
                    else -> "photo"
                }
            )

        private val NavBackStackEntry.photoId: String get() = get("id")!!
        private val NavBackStackEntry.isVideo: Boolean get() = get("type") == "video"
        private val NavBackStackEntry.center: Offset?
            get() {
                val x = get("centerX")?.toFloat()
                val y = get("centerY")?.toFloat()
                return if (x != null && y != null) Offset(x, y) else null
            }
        private val NavBackStackEntry.scale: Float get() = get("scale")?.toFloat() ?: 0.3f

        private fun NavBackStackEntry.get(arg: String) = arguments!!.getString(arg)
    }

    private fun Offset?.offsetFrom(size: IntSize) = when {
        this != null -> IntOffset((x - size.width / 2).toInt(), (y - size.height / 2).toInt())
        else -> IntOffset.Zero
    }
}
