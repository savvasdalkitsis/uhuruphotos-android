package com.savvasdalkitsis.librephotos.weblogin.mvflow

sealed class WebLoginAction {
    data class LoadPage(val url: String): WebLoginAction()
}