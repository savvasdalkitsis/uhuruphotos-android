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
package com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.*
import com.savvasdalkitsis.uhuruphotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.share.ShareImage
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class FeedPageEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val shareImage: ShareImage,
    private val toaster: Toaster,
) : EffectHandler<FeedPageEffect> {

    override suspend fun invoke(effect: FeedPageEffect) = when (effect) {
        ReloadApp -> with(controllersProvider.navController!!) {
            backQueue.clear()
            navigate(HomeNavigationTarget.name)
        }
        is OpenPhotoDetails -> navigateTo(
            PhotoNavigationTarget.name(effect.id, effect.center, effect.scale, effect.isVideo)
        )
        is SharePhotos -> {
            toaster.show("Downloading photos and will share soon")
            shareImage.shareMultiple(effect.selectedPhotos.mapNotNull {
                it.fullResUrl
            })
        }
        NavigateToServerEdit -> navigateTo(
            ServerNavigationTarget.name(auto = false)
        )
        Vibrate -> controllersProvider.haptics!!.performHapticFeedback(HapticFeedbackType.LongPress)
        NavigateToSettings -> navigateTo(SettingsNavigationTarget.name)
    }

    private fun navigateTo(target: String) {
        controllersProvider.navController!!.navigate(target)
    }
}