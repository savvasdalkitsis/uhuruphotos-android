package com.savvasdalkitsis.librephotos.server.viewmodel

import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect

class ServerEffectsHandler : (ServerEffect, NavHostController) -> Unit {

    override fun invoke(
        effect: ServerEffect,
        navController: NavHostController
    ) {
        when (effect) {
            ServerEffect.Close -> navController.popBackStack()
        }
    }
}