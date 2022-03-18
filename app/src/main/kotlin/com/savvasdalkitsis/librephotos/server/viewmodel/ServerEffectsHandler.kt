package com.savvasdalkitsis.librephotos.server.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.ErrorLoggingIn
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ServerEffectsHandler(
    private val context: Context
) : (ServerEffect, NavHostController) -> Unit {
    override fun invoke(
        effect: ServerEffect,
        navController: NavHostController
    ) {
        when (effect) {
            Close -> navController.popBackStack()
            is ErrorLoggingIn -> Toast.makeText(context, "There was an error logging in", Toast.LENGTH_LONG).show()
        }
    }
}