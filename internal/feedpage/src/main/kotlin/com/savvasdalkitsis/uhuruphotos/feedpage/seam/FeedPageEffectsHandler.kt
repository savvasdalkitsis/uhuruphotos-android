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
package com.savvasdalkitsis.uhuruphotos.feedpage.seam

import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.share.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.seam.FeedPageEffect.NavigateToServerEdit
import com.savvasdalkitsis.uhuruphotos.feedpage.seam.FeedPageEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.feedpage.seam.FeedPageEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.feedpage.seam.FeedPageEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.feedpage.seam.FeedPageEffect.SharePhotos
import com.savvasdalkitsis.uhuruphotos.feedpage.seam.FeedPageEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.photos.model.PhotoSequenceDataSource.AllPhotos
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.ui.usecase.UiUseCase
import javax.inject.Inject

internal class FeedPageEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val shareUseCase: ShareUseCase,
    private val toaster: Toaster,
    private val uiUseCase: UiUseCase,
) : EffectHandler<FeedPageEffect> {

    override suspend fun handleEffect(effect: FeedPageEffect) = when (effect) {
        ReloadApp -> with(controllersProvider.navController!!) {
            backQueue.clear()
            navigate(HomeNavigationRoutes.home)
        }
        is OpenPhotoDetails -> with(effect) {
            navigateTo(PhotoNavigationTarget.name(id, center, scale, isVideo, AllPhotos))
        }
        is SharePhotos -> {
            toaster.show(R.string.downloading_photos_sharing)
            shareUseCase.shareMultiple(effect.selectedPhotos.mapNotNull {
                it.fullResUrl
            })
        }
        NavigateToServerEdit -> navigateTo(
            ServerNavigationTarget.name(auto = false)
        )
        Vibrate -> uiUseCase.performLongPressHaptic()
        NavigateToSettings -> navigateTo(SettingsNavigationTarget.name)
    }

    private fun navigateTo(target: String) {
        controllersProvider.navController!!.navigate(target)
    }
}