package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect.*
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.librephotos.share.ShareImage
import com.savvasdalkitsis.librephotos.toaster.Toaster
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
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