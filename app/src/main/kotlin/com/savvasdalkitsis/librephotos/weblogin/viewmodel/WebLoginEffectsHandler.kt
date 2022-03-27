package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect

class WebLoginEffectsHandler : (WebLoginEffect, ControllersProvider) -> Unit {

    override fun invoke(effect: WebLoginEffect, controllersProvider: ControllersProvider) {
        TODO("Not yet implemented")
    }
}