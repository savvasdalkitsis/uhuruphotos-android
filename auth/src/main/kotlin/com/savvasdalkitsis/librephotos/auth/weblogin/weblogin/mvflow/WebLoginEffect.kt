package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow

sealed class WebLoginEffect {
    object Close : WebLoginEffect()
}