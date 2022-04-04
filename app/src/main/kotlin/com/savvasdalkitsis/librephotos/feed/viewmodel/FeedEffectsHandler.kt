package com.savvasdalkitsis.librephotos.feed.viewmodel

import androidx.compose.ui.ExperimentalComposeUiApi
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import javax.inject.Inject

class FeedEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : EffectHandler<FeedEffect>  {

    override fun invoke(feedEffect: FeedEffect) {
        when (feedEffect) {
            FeedEffect.ReloadApp -> {
                with(controllersProvider.navController!!) {
                    backQueue.clear()
                    navigate(HomeNavigationTarget.name)
                }
            }
        }
    }
}