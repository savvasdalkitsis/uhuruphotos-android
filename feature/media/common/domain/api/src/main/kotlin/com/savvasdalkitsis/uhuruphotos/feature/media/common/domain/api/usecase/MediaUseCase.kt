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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.*
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow

interface MediaUseCase {

    fun observeLocalMedia(): Flow<MediaItemsOnDeviceModel>

    fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDeviceModel>

    fun observeFavouriteMedia(): Flow<Result<List<MediaItemModel>, Throwable>>

    fun observeHiddenMedia(): Flow<Result<List<MediaItemModel>, Throwable>>

    suspend fun getFavouriteMediaCount(): Result<Long, Throwable>

    suspend fun getHiddenMedia(): Result<List<MediaItemModel>, Throwable>

    suspend fun setMediaItemFavourite(id: MediaIdModel<*>, favourite: Boolean): SimpleResult

    suspend fun refreshDetailsNowIfMissing(id: MediaIdModel<*>): Result<MediaOperationResultModel, Throwable>

    suspend fun refreshDetailsNow(id: MediaIdModel<*>): SimpleResult

    suspend fun refreshFavouriteMedia(): SimpleResult

    suspend fun refreshHiddenMedia(): SimpleResult

    fun trashMediaItem(id: MediaIdModel<*>)

    fun restoreMediaItem(id: MediaIdModel<*>)

    suspend fun List<MediaCollectionSourceModel>.toMediaCollections(): List<MediaCollectionModel>

    suspend fun toMediaCollection(groups: Group<String, MediaCollectionSourceModel>): List<MediaCollectionModel>
}