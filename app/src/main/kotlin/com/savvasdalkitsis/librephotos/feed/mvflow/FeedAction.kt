package com.savvasdalkitsis.librephotos.feed.mvflow

sealed class FeedAction {
    object LoadFeed : FeedAction()
    object UserBadgePressed : FeedAction()
    object DismissAccountOverview : FeedAction()
    object LogOut : FeedAction()
    object RefreshAlbums : FeedAction()
}
