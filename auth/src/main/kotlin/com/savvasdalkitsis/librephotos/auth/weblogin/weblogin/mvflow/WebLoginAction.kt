package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow

sealed class WebLoginAction {
    data class LoadPage(val url: String): WebLoginAction()
}