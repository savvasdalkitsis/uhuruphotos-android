package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.navigation

import android.util.Base64
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebEffectsHandler
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.view.WebLogin
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.view.WebLoginState
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.viewmodel.WebLoginViewModel
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import javax.inject.Inject

class WebLoginNavigationTarget @Inject constructor(
    private val effectsHandler: WebEffectsHandler,
) {

    fun NavGraphBuilder.create() {
        navigationTarget<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginViewModel>(
            name = name,
            effects = effectsHandler,
            initializer = { navBackStackEntry, actions ->
                actions(WebLoginAction.LoadPage(navBackStackEntry.url))
            },
            createModel = { hiltViewModel() }
        ) { state, _ ->
            WebLogin(state)
        }
    }

    companion object {
        private const val name = "web-login/{url}"
        fun name(url: String) = name.replace(
            "{url}", Base64.encodeToString(url.toByteArray(), Base64.URL_SAFE)
        )
        private val NavBackStackEntry.url : String get() {
            val encodedUrl = arguments!!.getString("url")!!
            val url = Base64.decode(encodedUrl, Base64.URL_SAFE)
            return String(url)
        }
    }
}