package com.savvasdalkitsis.librephotos.server.navigation

import android.app.Activity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.navigation.NavControllerProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerEffectsHandler
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerViewModel
import com.savvasdalkitsis.librephotos.server.view.ServerState
import javax.inject.Inject

class ServerNavigationTarget @Inject constructor(
    private val navControllerProvider: NavControllerProvider,
) {

    fun NavGraphBuilder.create(activity: Activity) =
        navigationTarget<ServerState, ServerAction, ServerEffect, ServerViewModel>(
            name = name,
            effects = ServerEffectsHandler(activity),
            viewBuilder = { state, actions -> Server(state, actions) },
            navController = navControllerProvider.navController!!,
        )

    companion object {
        const val name = "server"
    }
}