package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import androidx.navigation.NavHostController
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect

class WebLoginEffectsHandler : (WebLoginEffect, NavHostController) -> Unit {

    override fun invoke(effect: WebLoginEffect, navHostController: NavHostController) {
        TODO("Not yet implemented")
    }
}