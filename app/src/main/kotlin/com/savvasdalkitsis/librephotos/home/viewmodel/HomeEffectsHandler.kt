package com.savvasdalkitsis.librephotos.home.viewmodel

import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.server.navigation.serverNavigationTargetName

class HomeEffectsHandler : (HomeEffect, NavHostController) -> Unit {

    override fun invoke(
        effect: HomeEffect,
        navController: NavHostController
    ) = when (effect) {
        is HomeEffect.LaunchAuthentication -> navController.navigate(serverNavigationTargetName)
    }
}