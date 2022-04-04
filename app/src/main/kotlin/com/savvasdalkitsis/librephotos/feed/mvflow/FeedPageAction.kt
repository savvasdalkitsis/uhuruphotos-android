package com.savvasdalkitsis.librephotos.feed.mvflow

sealed class FeedPageAction {
    object LoadFeed : FeedPageAction()
    object UserBadgePressed : FeedPageAction()
    object DismissAccountOverview : FeedPageAction()
    object LogOut : FeedPageAction()
    object RefreshAlbums : FeedPageAction()
}
