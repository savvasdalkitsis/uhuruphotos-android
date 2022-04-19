package com.savvasdalkitsis.librephotos.server.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.RequestServerUrlChange
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
            initializer = { navBackStackEntry, action -> action(
                when {
                    navBackStackEntry.auto -> CheckPersistedServer
                    else -> RequestServerUrlChange
                })
            },
            content = { state, actions -> Server(state, actions) },
            createModel = { hiltViewModel() }
        )

    companion object {
        private const val name = "server/{auto}"
        fun name(auto: Boolean = true) = name
            .replace("{auto}", auto.toString())
        val NavBackStackEntry.auto get() = arguments?.getBoolean("{auto}", false) == true
    }
}