package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.feed.FeedItemSyncStatus

data class FeedItem(
    val stableId: String,
    val uri: String,
    val isVideo: Boolean,
    val syncStatus: FeedItemSyncStatus,
    val fallbackColor: String?,
    val isFavourite: Boolean,
    val ratio: Float,
)