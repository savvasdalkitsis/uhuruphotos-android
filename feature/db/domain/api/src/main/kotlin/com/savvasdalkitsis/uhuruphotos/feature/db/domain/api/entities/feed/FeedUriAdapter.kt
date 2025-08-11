package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.feed

import app.cash.sqldelight.ColumnAdapter
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedUri

object FeedUriAdapter : ColumnAdapter<FeedUri, String> {
    override fun decode(databaseValue: String): FeedUri = FeedUri(databaseValue)
    override fun encode(value: FeedUri): String = value.value
}