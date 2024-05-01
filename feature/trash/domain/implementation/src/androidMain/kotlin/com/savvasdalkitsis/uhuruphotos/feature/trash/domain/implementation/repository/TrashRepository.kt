/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetTrash
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrashQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.toTrash
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.service.TrashService
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class TrashRepository(
    private val remoteMediaTrashQueries: RemoteMediaTrashQueries,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val trashService: TrashService,
) {

    fun observeTrash(): Flow<Group<String, GetTrash>> =
        remoteMediaTrashQueries.getTrash().asFlow()
            .mapToList(Dispatchers.IO).groupBy(GetTrash::id)
            .distinctUntilChanged()

    suspend fun hasTrash(): Boolean =
        remoteMediaTrashQueries.count().awaitSingle() > 0

    suspend fun getTrash(): Group<String, GetTrash> =
        remoteMediaTrashQueries.getTrash().awaitList().groupBy(GetTrash::id).let(::Group)

    suspend fun refreshTrash(): SimpleResult = remoteMediaUseCase.refreshMediaCollections(
        incompleteMediaCollections = { trashService.getTrash() },
        clearCollectionsBeforeRefreshing = { remoteMediaTrashQueries.clear() },
        completeMediaCollection = { trashService.getTrashMediaCollection(it) },
        processSummary = { albumId, summary -> remoteMediaTrashQueries.insert(
            summary.toTrash(albumId)
        )}
    )

}