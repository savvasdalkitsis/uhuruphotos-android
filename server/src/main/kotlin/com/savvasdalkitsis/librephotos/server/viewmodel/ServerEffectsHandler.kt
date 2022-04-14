package com.savvasdalkitsis.librephotos.server.viewmodel

import android.content.Context
import android.widget.Toast
import com.savvasdalkitsis.librephotos.app.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.ErrorLoggingIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ServerEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    @ApplicationContext private val context: Context,
) : (ServerEffect) -> Unit {

    override fun invoke(
        effect: ServerEffect,
    ) {
        when (effect) {
            Close -> controllersProvider.navController!!.popBackStack()
            is ErrorLoggingIn -> Toast.makeText(context, "There was an error logging in", Toast.LENGTH_LONG).show()
        }
    }
}