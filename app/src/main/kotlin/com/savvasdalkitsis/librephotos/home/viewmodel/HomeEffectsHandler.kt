package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget

class HomeEffectsHandler : (HomeEffect, ControllersProvider) -> Unit {

    override fun invoke(
        effect: HomeEffect,
        controllersProvider: ControllersProvider,
    ) = when (effect) {
        is LaunchAuthentication -> controllersProvider.navController!!.navigate(ServerNavigationTarget.name)
    }
}