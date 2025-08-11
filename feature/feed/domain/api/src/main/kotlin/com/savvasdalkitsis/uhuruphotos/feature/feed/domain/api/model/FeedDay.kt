package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model

data class FeedDay(
    val date: String,
    val location: String?,
    val feedItems: List<Feed>
)