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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker.RemoteMediaItemWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemoteMediaUseCase @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val remoteMediaRepository: RemoteMediaRepository,
    private val remoteMediaItemWorkScheduler: RemoteMediaItemWorkScheduler,
    private val userUseCase: UserUseCase,
) : RemoteMediaUseCase {

    override fun String?.toRemoteUrl(): String? = this?.toAbsoluteRemoteUrl()

    @JvmName("toAbsoluteUrlNotNull")
    private fun String.toAbsoluteRemoteUrl(): String {
        val serverUrl = serverUseCase.getServerUrl()
        return this
            .removeSuffix(".webp")
            .removeSuffix(".mp4")
            .let { serverUrl + it }
    }

    override fun String?.toThumbnailUrlFromIdNullable(): String? =
        this?.toThumbnailUrlFromId()

    override fun String.toThumbnailUrlFromId(isVideo: Boolean): String =
        "/media/square_thumbnails/$this".toAbsoluteRemoteUrl()

    override fun String?.toFullSizeUrlFromIdNullable(isVideo: Boolean): String? =
        this?.toFullSizeUrlFromId(isVideo)

    override fun String.toFullSizeUrlFromId(isVideo: Boolean): String = when {
        isVideo -> "/media/video/$this".toAbsoluteRemoteUrl()
        else -> "/media/photos/$this".toAbsoluteRemoteUrl()
    }

    override fun observeAllPhotoDetails(): Flow<List<DbRemoteMediaItemDetails>> =
        remoteMediaRepository.observeAllMediaItemDetails()

    override fun observeFavouriteRemoteMedia(): Flow<Result<List<DbRemoteMediaItemSummary>>> =
        flow {
            emitAll(
                withFavouriteThreshold { threshold ->
                    remoteMediaRepository.observeFavouriteMedia(threshold)
                }.mapCatching { flow ->
                    flow.map { Result.success(it) }
                }.getOrElse {
                    flowOf(Result.failure(it))
                }
            )
        }

    override fun observeHiddenRemoteMedia(): Flow<List<DbRemoteMediaItemSummary>> =
        remoteMediaRepository.observeHiddenMedia()

    override suspend fun getRemoteMediaItemDetails(id: String): DbRemoteMediaItemDetails? =
        remoteMediaRepository.getMediaItemDetails(id)

    override suspend fun getFavouriteMediaSummaries(): Result<List<DbRemoteMediaItemSummary>> =
        withFavouriteThreshold {
            remoteMediaRepository.getFavouriteMedia(it)
        }

    override suspend fun getFavouriteMediaSummariesCount(): Result<Long> = withFavouriteThreshold {
        remoteMediaRepository.getFavouriteMediaCount(it)
    }

    override suspend fun getHiddenMediaSummaries(): List<DbRemoteMediaItemSummary> =
        remoteMediaRepository.getHiddenMedia()

    override suspend fun setMediaItemFavourite(id: String, favourite: Boolean): Result<Unit> =
        userUseCase.getUserOrRefresh().mapCatching { user ->
            remoteMediaRepository.setMediaItemRating(id, user.favoriteMinRating?.takeIf { favourite } ?: 0)
            remoteMediaItemWorkScheduler.scheduleMediaItemFavourite(id, favourite)
        }

    override suspend fun refreshDetailsNowIfMissing(id: String): Result<Unit> =
        remoteMediaRepository.refreshDetailsNowIfMissing(id)

    override suspend fun refreshDetailsNow(id: String): Result<Unit> =
        remoteMediaRepository.refreshDetailsNow(id)

    override suspend fun refreshFavouriteMedia(): Result<Unit> =
        resultWithFavouriteThreshold {
            remoteMediaRepository.refreshFavourites(it)
        }

    override fun downloadOriginal(id: String, video: Boolean) {
        remoteMediaItemWorkScheduler.scheduleMediaItemOriginalFileRetrieve(id, video)
    }

    override fun observeOriginalFileDownloadStatus(id: String): Flow<WorkInfo.State> =
        remoteMediaItemWorkScheduler.observeMediaItemOriginalFileRetrieveJobStatus(id)

    override suspend fun refreshHiddenMedia() =
        remoteMediaRepository.refreshHidden()

    override fun trashMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemTrashing(id)
    }

    override fun deleteMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemDeletion(id)
    }

    override fun restoreMediaItem(id: String) {
        remoteMediaItemWorkScheduler.scheduleMediaItemRestoration(id)
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T> =
        userUseCase.getUserOrRefresh().mapCatching {
            action(it.favoriteMinRating!!)
        }

    private suspend fun resultWithFavouriteThreshold(action: suspend (Int) -> Result<Unit>): Result<Unit> =
        userUseCase.getUserOrRefresh().mapCatching {
            action(it.favoriteMinRating!!).getOrThrow()
        }
}