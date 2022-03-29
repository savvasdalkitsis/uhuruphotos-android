package com.savvasdalkitsis.librephotos.weblogin.navigation

import android.util.Base64
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.auth.cookies.CookieMonitor
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.weblogin.view.WebLogin
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState
import com.savvasdalkitsis.librephotos.weblogin.viewmodel.WebLoginEffectsHandler
import com.savvasdalkitsis.librephotos.weblogin.viewmodel.WebLoginViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class WebLoginNavigationTarget @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val cookieMonitor: CookieMonitor,
) {

    fun NavGraphBuilder.create() {
        val navController = controllersProvider.navController!!
        navigationTarget<WebLoginState, WebLoginAction, WebLoginEffect, WebLoginViewModel>(
            name = name,
            effects = WebLoginEffectsHandler(),
            viewBuilder = { state, actions, _ ->
                WebLogin(state, actions)
            },
            initializer = { navBackStackEntry, actions ->
                val encodedUrl = navBackStackEntry.arguments!!.getString("url")!!
                val url = Base64.decode(encodedUrl, Base64.URL_SAFE)
                cookieMonitor.monitor().invokeOnCompletion {
                    Dispatchers.Main.dispatch(Dispatchers.Main) {
                        navController.popBackStack()
                    }
                }
                actions(WebLoginAction.LoadPage(String(url)))
            },
            controllersProvider = controllersProvider,
        )
    }

    companion object {
        private const val name = "web-login/{url}"
        fun name(url: String) = name.replace(
            "{url}", Base64.encodeToString(url.toByteArray(), Base64.URL_SAFE)
        )
    }
}