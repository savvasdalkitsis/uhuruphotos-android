package com.savvasdalkitsis.librephotos.weblogin.view

import android.annotation.SuppressLint
import android.webkit.CookieManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.savvasdalkitsis.librephotos.main.view.MainScaffold
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginAction

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebLogin(state: WebLoginState) {
    if (state.url == null) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        val webState = rememberWebViewState(url = state.url)
        MainScaffold { contentPadding ->
            Column {
            Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                WebView(
                    webState,
                    onCreated = {
                        CookieManager.getInstance().setAcceptThirdPartyCookies(it, true)
                        with(it.settings) {
                            userAgentString = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"
                            javaScriptEnabled = true
                        }
                    }
                )
            }
        }
    }
}