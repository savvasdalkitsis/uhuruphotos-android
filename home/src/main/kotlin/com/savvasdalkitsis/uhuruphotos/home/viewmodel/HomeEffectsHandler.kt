package com.savvasdalkitsis.uhuruphotos.home.viewmodel

import com.savvasdalkitsis.uhuruphotos.home.module.HomeModule
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect.LoadFeed
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import javax.inject.Inject

class HomeEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    @HomeModule.HomeNavigationTargetFeed private val feedNavigationTargetName: String,
) : (HomeEffect) -> Unit {

    override fun invoke(
        effect: HomeEffect,
    ) {
        with(controllersProvider.navController!!) {
            when (effect) {
                LaunchAuthentication -> navigate(ServerNavigationTarget.name())
                LoadFeed -> {
                    popBackStack()
                    navigate(feedNavigationTargetName)
                }
            }
        }
    }
}