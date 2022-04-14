package com.savvasdalkitsis.librephotos.server.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.app.navigation.navigationTarget
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

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    fun NavGraphBuilder.create() =
        navigationTarget<ServerState, ServerEffect, ServerAction, ServerViewModel>(
            name = name,
            effects = effectsHandler,
            initializer = {_, action -> action(CheckPersistedServer) },
            content = { state, actions -> Server(state, actions) },
            createModel = { hiltViewModel() }
        )

    companion object {
        const val name = "server"
    }
}