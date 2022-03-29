package com.savvasdalkitsis.librephotos.server.navigation

import android.content.Context
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerEffectsHandler
import com.savvasdalkitsis.librephotos.server.viewmodel.ServerViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ServerNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
    @ApplicationContext private val context: Context,
) {

    fun NavGraphBuilder.create() =
        navigationTarget<ServerState, ServerAction, ServerEffect, ServerViewModel>(
            name = name,
            effects = ServerEffectsHandler(context),
            viewBuilder = { state, actions, _ -> Server(state, actions) },
            controllersProvider = controllersProvider,
        )

    companion object {
        const val name = "server"
    }
}