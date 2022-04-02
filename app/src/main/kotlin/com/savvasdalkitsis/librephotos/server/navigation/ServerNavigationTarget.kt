package com.savvasdalkitsis.librephotos.server.navigation

import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerEffectsHandler
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerViewModel
import javax.inject.Inject

class ServerNavigationTarget @Inject constructor(
    private val effectsHandler: ServerEffectsHandler,
) {

    fun NavGraphBuilder.create() =
        navigationTarget<ServerState, ServerEffect, ServerAction, ServerViewModel>(
            name = name,
            effects = effectsHandler,
            initializer = {_, action -> action(CheckPersistedServer) },
            content = { state, actions -> Server(state, actions) },
        )

    companion object {
        const val name = "server"
    }
}