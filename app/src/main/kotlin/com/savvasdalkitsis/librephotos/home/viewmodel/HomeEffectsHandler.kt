package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.feed.navigation.FeedNavigationTarget
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget

class HomeEffectsHandler : (HomeEffect, ControllersProvider) -> Unit {

    override fun invoke(
        effect: HomeEffect,
        controllersProvider: ControllersProvider,
    ) {
        controllersProvider.navController!!.navigate(
            when (effect) {
                LaunchAuthentication -> ServerNavigationTarget.name
                HomeEffect.LoadFeed -> FeedNavigationTarget.name
            }
        )
    }
}