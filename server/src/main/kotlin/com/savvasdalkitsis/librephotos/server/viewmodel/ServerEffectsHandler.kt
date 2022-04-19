package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.librephotos.toaster.Toaster
import javax.inject.Inject

class ServerEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val toaster: Toaster,
) : (ServerEffect) -> Unit {

    override fun invoke(
        effect: ServerEffect,
    ) {
        when (effect) {
            Close -> controllersProvider.navController!!.popBackStack()
            is ErrorLoggingIn -> toaster.show("There was an error logging in")
        }
    }
}