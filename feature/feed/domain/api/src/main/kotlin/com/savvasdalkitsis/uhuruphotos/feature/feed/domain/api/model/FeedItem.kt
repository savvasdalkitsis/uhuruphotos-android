package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus

data class FeedItem(
    val stableId: String,
    val uri: String,
    val isVideo: Boolean,
    val syncStatus: FeedItemSyncStatus,
    val fallbackColor: Int?,
    val isFavourite: Boolean,
    val ratio: Float,
)