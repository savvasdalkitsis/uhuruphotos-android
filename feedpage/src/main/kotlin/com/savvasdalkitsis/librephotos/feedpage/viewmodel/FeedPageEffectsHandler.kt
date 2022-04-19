package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import android.content.Context
import android.widget.Toast
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect.*
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.librephotos.share.ShareImage
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FeedPageEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val shareImage: ShareImage,
    @ApplicationContext private val context: Context,
) : EffectHandler<FeedPageEffect> {

    override suspend fun invoke(effect: FeedPageEffect) = when (effect) {
        ReloadApp -> with(controllersProvider.navController!!) {
            backQueue.clear()
            navigate(HomeNavigationTarget.name)
        }
        is OpenPhotoDetails -> controllersProvider.navController!!.navigate(
            when {
                effect.isVideo ->
                    PhotoNavigationTarget.photo(effect.id, effect.center, effect.scale)
                else ->
                    PhotoNavigationTarget.video(effect.id, effect.center, effect.scale)
            }
        )
        is SharePhotos -> {
            Toast.makeText(context, "Downloading photos and will share soon", Toast.LENGTH_LONG).show()
            shareImage.shareMultiple(effect.selectedPhotos.mapNotNull {
                it.fullResUrl
            })
        }
    }
}