package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect.*
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.photos.navigation.PhotoNavigationTarget
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
        is OpenPhotoDetails -> controllersProvider.navController!!.navigate(
            PhotoNavigationTarget.name(effect.id, effect.center, effect.scale, effect.isVideo)
        )
        is SharePhotos -> {
            toaster.show("Downloading photos and will share soon")
            shareImage.shareMultiple(effect.selectedPhotos.mapNotNull {
                it.fullResUrl
            })
        }
    }
}