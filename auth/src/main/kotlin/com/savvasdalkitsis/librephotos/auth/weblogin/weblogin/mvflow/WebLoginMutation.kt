package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow

sealed class WebLoginMutation {

    data class LoadPage(val url: String) : WebLoginMutation()
    object Loading : WebLoginMutation()
}