package com.savvasdalkitsis.librephotos.server.viewmodel

import android.content.ContentProvider
import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.ErrorLoggingIn
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ServerEffectsHandler(
    private val context: Context
) : (ServerEffect, ControllersProvider) -> Unit {

    override fun invoke(
        effect: ServerEffect,
        controllersProvider: ControllersProvider,
    ) {
        when (effect) {
            Close -> controllersProvider.navController!!.popBackStack()
            is ErrorLoggingIn -> Toast.makeText(context, "There was an error logging in", Toast.LENGTH_LONG).show()
        }
    }
}