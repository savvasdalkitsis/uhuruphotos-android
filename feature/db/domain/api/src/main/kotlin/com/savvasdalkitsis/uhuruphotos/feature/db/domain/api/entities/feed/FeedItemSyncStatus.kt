package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.feed

enum class FeedItemSyncStatus(
    val stableId: Int, // must never change since feed table will be wrong
) {
    LOCAL_ONLY(0),
    REMOTE_ONLY(1),
    LOCAL_UPLOADING(2),
    REMOTE_DOWNLOADING(3),
    FULLY_SYNCED(4)
}