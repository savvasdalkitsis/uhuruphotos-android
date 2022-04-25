package com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow

sealed class WebLoginEffect {
    object Close : WebLoginEffect()
}