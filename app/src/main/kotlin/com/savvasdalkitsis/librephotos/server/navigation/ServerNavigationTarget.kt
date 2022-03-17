package com.savvasdalkitsis.librephotos.server.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerEffectsHandler
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerViewModel
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState

const val serverNavigationTargetName = "server"

fun NavGraphBuilder.serverNavigationTarget(navController: NavHostController) =
    navigationTarget<ServerState, ServerAction, ServerEffect, ServerViewModel>(
        name = serverNavigationTargetName,
        effects = ServerEffectsHandler(),
        viewBuilder = { state, actions -> Server(state, actions) },
        navController,
    )