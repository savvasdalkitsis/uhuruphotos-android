package com.savvasdalkitsis.uhuruphotos.server.viewmodel

import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
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