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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.center
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.datasource
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.imageSource
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.isVideo
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.offsetFrom
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.photoId
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget.scale
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.LoadPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.Photo
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PhotoNavigationTarget @Inject constructor(
    private val effectsHandler: PhotoEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<PhotoState, PhotoEffect, PhotoAction, PhotoViewModel>(
            name = PhotoNavigationTarget.registrationName,
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
                    actions(LoadPhoto(photoId, isVideo, datasource, imageSource))
                }
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Photo(state, actions)
        }
    }
}
