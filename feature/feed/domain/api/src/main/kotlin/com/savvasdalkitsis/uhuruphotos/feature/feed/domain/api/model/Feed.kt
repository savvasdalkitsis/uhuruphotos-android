package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model

data class Feed(
    val days: List<FeedDay>
) {
    val totalItems: Int = days.sumOf { it.feedItems.size }
}