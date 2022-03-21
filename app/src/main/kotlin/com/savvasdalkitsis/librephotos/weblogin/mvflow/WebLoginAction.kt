package com.savvasdalkitsis.librephotos.weblogin.mvflow

sealed class WebLoginAction {
    object Loading : WebLoginAction()
    data class LoadPage(val url: String): WebLoginAction()
}