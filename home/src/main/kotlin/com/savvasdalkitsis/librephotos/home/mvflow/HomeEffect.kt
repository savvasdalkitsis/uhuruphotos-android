package com.savvasdalkitsis.librephotos.home.mvflow

sealed class HomeEffect {
    object LaunchAuthentication : HomeEffect()
    object LoadFeed : HomeEffect()
}
