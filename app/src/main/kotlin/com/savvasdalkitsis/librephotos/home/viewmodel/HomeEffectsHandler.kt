package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LoadFeed
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import javax.inject.Inject

class HomeEffectsHandler @Inject constructor(
    private val controllersProvider: com.savvasdalkitsis.librephotos.navigation.ControllersProvider,
) : (HomeEffect) -> Unit {

    override fun invoke(
        effect: HomeEffect,
    ) {
        with(controllersProvider.navController!!) {
            when (effect) {
                LaunchAuthentication -> navigate(ServerNavigationTarget.name)
                LoadFeed -> {
                    popBackStack()
                    navigate(FeedPageNavigationTarget.name)
                }
            }
        }
    }
}