package com.savvasdalkitsis.librephotos.server.navigation

import android.app.Activity
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerEffectsHandler
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerViewModel
import com.savvasdalkitsis.librephotos.server.view.ServerState
import javax.inject.Inject

class ServerNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
) {

    fun NavGraphBuilder.create(activity: Activity) =
        navigationTarget<ServerState, ServerAction, ServerEffect, ServerViewModel>(
            name = name,
            effects = ServerEffectsHandler(activity),
            viewBuilder = { state, actions -> Server(state, actions) },
            controllersProvider = controllersProvider,
        )

    companion object {
        const val name = "server"
    }
}