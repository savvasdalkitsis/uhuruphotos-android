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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.photos.model.PhotoSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.implementation.photos.model.PhotoSequenceDataSource.Single
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.LoadPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.Photo
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.viewmodel.PhotoViewModel
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PhotoNavigationTarget @Inject constructor(
    private val effectsHandler: PhotoEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
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
                with(navBackStackEntry) {
                    actions(LoadPhoto(photoId, isVideo, datasource))
                }
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Photo(state, actions)
        }
    }

    companion object {
        private const val name = "details/{type}/{id}/{centerX}/{centerY}/{scale}/{dataSource}"
        fun name(
            id: String,
            offset: Offset,
            scale: Float,
            isVideo: Boolean,
            photoSequenceDataSource: PhotoSequenceDataSource = Single,
        ) = name
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
            .replace("{dataSource}", photoSequenceDataSource.toArgument)

        private val NavBackStackEntry.datasource: PhotoSequenceDataSource get() =
            PhotoSequenceDataSource.from(get("dataSource").orEmpty())

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
