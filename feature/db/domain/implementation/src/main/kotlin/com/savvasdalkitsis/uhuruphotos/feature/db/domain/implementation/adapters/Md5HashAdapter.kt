package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.adapters

import app.cash.sqldelight.ColumnAdapter
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash

object Md5HashAdapter : ColumnAdapter<Md5Hash, String> {

    override fun decode(databaseValue: String): Md5Hash =
        Md5Hash(databaseValue)

    override fun encode(value: Md5Hash): String = value.value
}