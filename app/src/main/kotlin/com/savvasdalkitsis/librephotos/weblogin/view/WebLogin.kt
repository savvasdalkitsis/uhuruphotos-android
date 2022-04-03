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
                            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.58 Mobile Safari/537.36"
                            javaScriptEnabled = true
                        }
                    }
                )
            }
        }
    }
}