package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.photos.navigation.PhotoNavigationTarget
import javax.inject.Inject

class FeedPageEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : com.savvasdalkitsis.librephotos.viewmodel.EffectHandler<FeedPageEffect> {

    override suspend fun invoke(effect: FeedPageEffect) = when (effect) {
        FeedPageEffect.ReloadApp -> {
            with(controllersProvider.navController!!) {
                backQueue.clear()
                navigate(HomeNavigationTarget.name)
            }
        }
        is FeedPageEffect.OpenPhotoDetails ->
            controllersProvider.navController!!.navigate(
                when {
                    effect.isVideo ->
                        PhotoNavigationTarget.photo(effect.id, effect.center, effect.scale)
                    else ->
                        PhotoNavigationTarget.video(effect.id, effect.center, effect.scale)
                })
    }
}