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
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.UserAlbumService
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.model.UserAlbumEditModel
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.GetUserAlbumMedia
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.NewUserAlbumAdditionQueueQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumAdditionQueueQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
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
    private val userAlbumsQueries: UserAlbumsQueries,
    private val userAlbumAdditionQueueQueries: UserAlbumAdditionQueueQueries,
    private val newUserAlbumAdditionQueueQueries: NewUserAlbumAdditionQueueQueries,
    private val userAlbumPhotosQueries: UserAlbumPhotosQueries,
    private val userAlbumService: UserAlbumService,
) {

    suspend fun getUserAlbum(albumId: Int): List<GetUserAlbumMedia> =
        userAlbumQueries.getUserAlbumMedia(albumId.toString()).awaitList()

    suspend fun getMediaAdditionQueue(albumId: Int): Result<List<String>, Throwable> = runCatchingWithLog {
        userAlbumAdditionQueueQueries.getQueueFor(albumId).awaitList()
    }

    fun removeMediaFromAdditionQueue(albumId: Int, ids: List<String>): SimpleResult = runCatchingWithLog {
        db.transaction {
            for (id in ids) {
                userAlbumAdditionQueueQueries.remove(albumId, id)
            }
        }
    }

    fun queueMediaAdditionToUserAlbum(albumId: Int, ids: List<String>): SimpleResult = runCatchingWithLog {
        db.transaction {
            for (mediaId in ids) {
                userAlbumAdditionQueueQueries.insert(albumId, mediaId)
            }
        }
    }

    fun queueMediaAdditionToNewUserAlbum(albumName: String, ids: List<String>): SimpleResult = runCatchingWithLog {
        db.transaction {
            for (mediaId in ids) {
                newUserAlbumAdditionQueueQueries.insert(albumName, mediaId)
            }
        }
    }

    fun migrateQueueFromNewAlbum(name: String, albumId: Int) {
        db.transaction {
            newUserAlbumAdditionQueueQueries.migrate(albumId = albumId, albumName = name)
            newUserAlbumAdditionQueueQueries.removeForName(name)
        }
    }

    suspend fun addMediaToUserAlbum(albumId: Int, ids: List<String>): SimpleResult = runCatchingWithLog {
        val album = userAlbumQueries.getUserAlbum(albumId.toString()).awaitSingleOrNull()
            ?: throw IllegalArgumentException("Album $albumId not found")
        val title = album.title ?: throw IllegalStateException("Album $albumId has no title")
        val newAlbum =
            userAlbumService.addMediaToUserAlbum(albumId.toString(), UserAlbumEditModel(title, ids))
        db.transaction {
            userAlbumPhotosQueries.removePhotosForAlbum(albumId.toString())
            for (id in newAlbum.ids) {
                userAlbumPhotosQueries.insert(id, albumId.toString())
            }
        }
    }.simple()

    fun observeUserAlbum(albumId: Int): Flow<List<GetUserAlbumMedia>> =
        userAlbumQueries.getUserAlbumMedia(albumId.toString())
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
            for (summary in album.groups.flatMap { it.items }) {
                if (!summary.removed && !summary.inTrash) {
                    userAlbumPhotosQueries.insert(summary.id, albumId.toString())
                }
            }
        }
    }.simple()

    suspend fun createNewUserAlbum(name: String) = runCatchingWithLog {
        userAlbumService.createUserAlbum(UserAlbumEditModel(title = name, ids = emptyList())).also { album ->
            userAlbumQueries.insert(
                UserAlbum(
                    id = album.id.toString(),
                    title = album.title,
                    date = album.date,
                    location = null,
                )
            )
        }
    }

    suspend fun deleteUserAlbum(albumId: Int): SimpleResult = runCatchingWithLog {
        userAlbumService.deleteUserAlbum(albumId.toString())
        userAlbumPhotosQueries.removePhotosForAlbum(albumId.toString())
        userAlbumQueries.remove(albumId.toString())
        userAlbumsQueries.removeAlbum(albumId)
    }.simple()
}