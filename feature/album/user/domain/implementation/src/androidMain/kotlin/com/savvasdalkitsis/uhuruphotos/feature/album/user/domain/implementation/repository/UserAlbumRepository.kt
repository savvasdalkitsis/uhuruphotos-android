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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.UserAlbumService
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class UserAlbumRepository @Inject constructor(
    private val db: Database,
    private val userAlbumQueries: UserAlbumQueries,
    private val userAlbumPhotosQueries: UserAlbumPhotosQueries,
    private val userAlbumService: UserAlbumService,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
) {

    suspend fun getUserAlbum(albumId: Int): List<GetUserAlbum> =
        userAlbumQueries.getUserAlbum(albumId.toString()).awaitList()

    fun observeUserAlbum(albumId: Int): Flow<List<GetUserAlbum>> =
        userAlbumQueries.getUserAlbum(albumId.toString())
            .asFlow().mapToList(Dispatchers.IO)
            .distinctUntilChanged()

    suspend fun refreshUserAlbum(albumId: Int): SimpleResult = runCatchingWithLog {
        val album = userAlbumService.getUserAlbum(albumId.toString())
        db.transaction {
            userAlbumQueries.insert(
                UserAlbum(
                    id = albumId.toString(),
                    title = album.title,
                    date = album.date,
                    location = album.location,
                )
            )
            userAlbumPhotosQueries.removePhotosForAlbum(albumId.toString())
            for (photo in album.groups.flatMap { it.photos }) {
                remoteMediaItemSummaryQueries.insert(photo.toDbModel(albumId.toString()))
                userAlbumPhotosQueries.insert(photo.id, albumId.toString())
            }
        }
    }.simple()
}