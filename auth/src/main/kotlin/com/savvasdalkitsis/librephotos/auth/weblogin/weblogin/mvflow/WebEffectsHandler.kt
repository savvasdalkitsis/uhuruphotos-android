package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow

import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import javax.inject.Inject

class WebEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
): (WebLoginEffect) -> Unit {

    override fun invoke(effect: WebLoginEffect) {
        when (effect) {
            WebLoginEffect.Close -> controllersProvider.navController!!.popBackStack()
        }
    }
}
