package com.savvasdalkitsis.uhuruphotos.home.mvflow

sealed class HomeEffect {
    object LaunchAuthentication : HomeEffect()
    object LoadFeed : HomeEffect()
}
