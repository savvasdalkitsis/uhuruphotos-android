package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.feed

import app.cash.sqldelight.ColumnAdapter
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus

object FeedItemSyncStatusAdapter: ColumnAdapter<FeedItemSyncStatus, Long> {
    override fun decode(databaseValue: Long): FeedItemSyncStatus = FeedItemSyncStatus.entries.firstOrNull {
        it.stableId == databaseValue
    } ?: throw IllegalArgumentException("Unknown FeedItemSyncStatus: $databaseValue")

    override fun encode(value: FeedItemSyncStatus): Long = value.stableId
}