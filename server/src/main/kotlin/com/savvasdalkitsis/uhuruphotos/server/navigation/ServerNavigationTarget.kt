package com.savvasdalkitsis.uhuruphotos.server.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.uhuruphotos.server.view.Server
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.server.viewmodel.ServerEffectsHandler
import com.savvasdalkitsis.uhuruphotos.server.viewmodel.ServerViewModel
import javax.inject.Inject

class ServerNavigationTarget @Inject constructor(
    private val effectsHandler: ServerEffectsHandler,
) : NavigationTarget {

    override fun NavGraphBuilder.create() =
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
        val NavBackStackEntry.auto get() = arguments?.getString("auto") == "true"
    }
}