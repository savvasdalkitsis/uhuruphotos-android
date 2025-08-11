package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

enum class FeedItemSyncStatus(
    val stableId: Long, // must never change since feed table will be wrong
) {
    LOCAL_ONLY(0),
    REMOTE_ONLY(1),
    LOCAL_UPLOADING(2),
    REMOTE_DOWNLOADING(3),
    FULLY_SYNCED(4);
}