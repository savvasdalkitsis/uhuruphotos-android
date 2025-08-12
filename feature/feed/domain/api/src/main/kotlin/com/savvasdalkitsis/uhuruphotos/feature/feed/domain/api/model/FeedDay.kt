package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.feed.Feed

data class FeedDay(
    val date: String,
    val location: String?,
    val feedItems: List<Feed>
)