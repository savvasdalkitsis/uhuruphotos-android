package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.adapters

import app.cash.sqldelight.ColumnAdapter
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses

object UploadStatusAdapter : ColumnAdapter<UploadStatuses, String> {

    override fun decode(databaseValue: String): UploadStatuses =
        UploadStatuses.entries.firstOrNull {
            it.stableId == databaseValue.toIntOrNull()
        } ?: throw IllegalArgumentException("Unknown UploadStatus: $databaseValue")

    override fun encode(value: UploadStatuses): String = value.stableId.toString()
}