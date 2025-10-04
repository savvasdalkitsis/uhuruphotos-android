package com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.repository

import android.content.Context
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.sync.Sync
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.sync.SyncQueries
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncQueries: SyncQueries,
) {

    fun observeSync(): Flow<List<UploadItem>> = syncQueries.get().asFlow()
        .mapToList(Dispatchers.IO)
        .map { items ->
            items.toUploadItems()
        }.distinctUntilChanged()

    suspend fun getSync(): List<UploadItem> =
        syncQueries.get().awaitList().toUploadItems()

    private fun List<Sync>.toUploadItems(): List<UploadItem> = map { item ->
        UploadItem(
            item.id,
            item.uri.resolve(
                item.md5sum,
                "",
                null,
                item.isVideo,
                context,
                isThumbnail = false
            )
        )
    }
}
