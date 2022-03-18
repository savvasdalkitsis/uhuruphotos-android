package com.savvasdalkitsis.librephotos.server.navigation

import android.app.Activity
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

fun NavGraphBuilder.serverNavigationTarget(navController: NavHostController, activity: Activity) =
    navigationTarget<ServerState, ServerAction, ServerEffect, ServerViewModel>(
        name = serverNavigationTargetName,
        effects = ServerEffectsHandler(activity),
        viewBuilder = { state, actions -> Server(state, actions) },
        navController,
    )