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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.latLng
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.*
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker.RemoteMediaItemWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.toLatLon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaUseCase @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val remoteMediaRepository: RemoteMediaRepository,
    private val remoteMediaItemWorkScheduler: RemoteMediaItemWorkScheduler,
    private val userUseCase: UserUseCase,
    private val dateDisplayer: DateDisplayer,
    private val peopleUseCase: PeopleUseCase,
) : MediaUseCase {

    override fun MediaId<*>.toThumbnailUriFromId(isVideo: Boolean): String =
        when (this) {
            is Remote -> with(remoteMediaUseCase) {
                value.toThumbnailUrlFromId(isVideo)
            }
            is Local -> with(localMediaUseCase) {
                value.toContentUri(isVideo)
            }
            is MediaId.Group -> when (val id = preferLocal) {
                is Remote -> with(remoteMediaUseCase) {
                    id.value.toThumbnailUrlFromId(isVideo)
                }
                is Local -> with(localMediaUseCase) {
                    id.value.toContentUri(isVideo)
                }
                else -> ""
            }
        }

    override fun MediaId<*>.toFullSizeUriFromId(isVideo: Boolean): String =
        when (this) {
            is Remote -> with(remoteMediaUseCase) {
                value.toFullSizeUrlFromId(isVideo)
            }
            is Local -> with(localMediaUseCase) {
                value.toContentUri(isVideo)
            }
            is MediaId.Group -> when (val id = preferLocal) {
                is Remote -> with(remoteMediaUseCase) {
                    id.value.toFullSizeUrlFromId(isVideo)
                }
                is Local -> with(localMediaUseCase) {
                    id.value.toContentUri(isVideo)
                }
                else -> ""
            }
        }

    override fun observeLocalMedia(): Flow<MediaItemsOnDevice> =
        combine(
            localMediaUseCase.observeLocalMediaItems(),
            userUseCase.observeUser(),
        ) { localMediaItems, user ->
            combineLocalMediaItemsWithUser(localMediaItems, user)
        }

    override suspend fun getLocalMedia(): MediaItemsOnDevice {
        val userResult = userUseCase.getUserOrRefresh()
        return userResult.map { user ->
            val localMedia = localMediaUseCase.getLocalMediaItems()
            combineLocalMediaItemsWithUser(localMedia, user)
        }.getOrElse {
            MediaItemsOnDevice.Error
        }
    }

    private fun combineLocalMediaItemsWithUser(
        localMediaItems: LocalMediaItems,
        user: User
    ) = when (localMediaItems) {
        is LocalMediaItems.Found -> {
            MediaItemsOnDevice.Found(
                primaryFolder = localMediaItems.primaryLocalMediaFolder?.let { (folder, items) ->
                    folder to items.map { it.toMediaItem(user.id) }
                },
                mediaFolders = localMediaItems.localMediaFolders.map { (folder, items) ->
                    folder to items.map { it.toMediaItem(user.id) }
                }
            )
        }

        is LocalMediaItems.RequiresPermissions -> MediaItemsOnDevice.RequiresPermissions(
            localMediaItems.deniedPermissions
        )
    }

    override fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDevice> =
        combine(
            localMediaUseCase.observeLocalMediaFolder(albumId),
            userUseCase.observeUser(),
        ) { mediaItems, user ->
            when (mediaItems) {
                is LocalFolder.Found -> {
                    MediaFolderOnDevice.Found(
                        mediaItems.bucket.first to mediaItems.bucket.second.map {
                            it.toMediaItem(user.id)
                        }
                    )
                }
                is LocalFolder.RequiresPermissions -> MediaFolderOnDevice.RequiresPermissions(
                    mediaItems.deniedPermissions
                )
                LocalFolder.Error -> MediaFolderOnDevice.Error
            }
        }

    override fun observeFavouriteMedia(): Flow<Result<List<MediaItem>>> =
        remoteMediaUseCase.observeFavouriteRemoteMedia()
            .map { media ->
                media.mapCatching {
                    it.mapToMediaItems().getOrThrow()
                }
            }

    override fun observeHiddenMedia(): Flow<Result<List<MediaItem>>> =
            remoteMediaUseCase.observeHiddenRemoteMedia()
                .mapToPhotos()

    private fun Flow<List<DbRemoteMediaItemSummary>>.mapToPhotos(): Flow<Result<List<MediaItem>>> =
        map { it.mapToMediaItems() }

    override suspend fun List<DbRemoteMediaItemSummary>.mapToMediaItems(): Result<List<MediaItem>> =
        withFavouriteThreshold { threshold ->
            map {
                with(remoteMediaUseCase) {
                    val date = dateDisplayer.dateString(it.date)
                    MediaItemInstance(
                        id = Remote(it.id),
                        mediaHash = it.id,
                        thumbnailUri = it.id.toThumbnailUrlFromIdNullable(),
                        fullResUri = it.id.toFullSizeUrlFromId(it.isVideo),
                        fallbackColor = it.dominantColor,
                        isFavourite = (it.rating ?: 0) >= threshold,
                        displayDayDate = date,
                        sortableDate = it.date,
                        ratio = it.aspectRatio ?: 1f,
                        isVideo = it.isVideo,
                        syncState = REMOTE_ONLY,
                    )
                }
            }
        }

    private fun LocalMediaItem.toMediaItem(userId: Int) = MediaItemInstance(
        id = Local(id),
        mediaHash = md5 + userId,
        thumbnailUri = contentUri,
        fullResUri = contentUri,
        fallbackColor = fallbackColor,
        isFavourite = false,
        displayDayDate = displayDate,
        sortableDate = sortableDate,
        ratio = (width / height.toFloat()).takeIf { it > 0 } ?: 1f,
        isVideo = video,
        latLng = latLon,
        syncState = LOCAL_ONLY,
    )

    override suspend fun getMediaItemDetails(id: MediaId<*>): MediaItemDetails? = when (id) {
        is Remote -> remoteMediaUseCase.getRemoteMediaItemDetails(id.value)
            ?.toMediaItemDetails()
        is Local -> localMediaUseCase.getLocalMediaItem(id.value)
            ?.toMediaItemDetails()
        is MediaId.Group -> when (val preferred = id.preferRemote) {
            is Remote -> remoteMediaUseCase.getRemoteMediaItemDetails(preferred.value)
                ?.toMediaItemDetails()
            is Local -> localMediaUseCase.getLocalMediaItem(preferred.value)
                ?.toMediaItemDetails()
            else -> null
        }
    }

    private fun LocalMediaItem.toMediaItemDetails(): MediaItemDetails =
        MediaItemDetails(
            formattedDateAndTime = displayDateTime,
            isFavourite = false,
            isVideo = video,
            location = "",
            latLon = latLon?.let { (lat, lon) -> LatLon(lat, lon) },
            path = path ?: contentUri,
            peopleInMediaItem = emptyList(),
        )

    private suspend fun DbRemoteMediaItemDetails.toMediaItemDetails(): MediaItemDetails {
        val favouriteThreshold = userUseCase.getUserOrRefresh().getOrNull()?.favoriteMinRating
        val people = peopleUseCase.getPeopleByName().ifEmpty {
            peopleUseCase.refreshPeople()
            peopleUseCase.getPeopleByName()
        }
        val peopleNamesList = peopleNames.orEmpty().deserializePeopleNames
        val peopleInPhoto = people
            .filter { it.name in peopleNamesList }
            .map {
                with(remoteMediaUseCase) {
                    it.toPerson { person ->
                        person.toRemoteUrl()
                    }
                }
            }
        return MediaItemDetails(
            formattedDateAndTime = dateDisplayer.dateTimeString(timestamp),
            isFavourite = favouriteThreshold != null && (rating ?: 0) >= favouriteThreshold,
            isVideo = video == true,
            location = location.orEmpty(),
            latLon = latLng?.toLatLon,
            path = imagePath,
            peopleInMediaItem = peopleInPhoto,
        )
    }

    override suspend fun getFavouriteMedia(): Result<List<MediaItem>> =
        remoteMediaUseCase.getFavouriteMediaSummaries()
            .mapCatching {
                it.mapToMediaItems().getOrThrow()
            }

    override suspend fun getFavouriteMediaCount(): Result<Long> =
        remoteMediaUseCase.getFavouriteMediaSummariesCount()

    override suspend fun getHiddenMedia(): Result<List<MediaItem>> =
        remoteMediaUseCase.getHiddenMediaSummaries().mapToMediaItems()

    override suspend fun setMediaItemFavourite(id: MediaId<*>, favourite: Boolean): Result<Unit> =
        when (id) {
            is Remote -> setRemoteMediaFavourite(id, favourite)
            is MediaId.Group -> {
                when (val preferred = id.preferRemote) {
                    is Remote -> setRemoteMediaFavourite(preferred, favourite)
                    else -> Result.success(Unit)
                }
            }
            else -> Result.success(Unit)
        }

    private suspend fun setRemoteMediaFavourite(
        id: Remote,
        favourite: Boolean
    ) = userUseCase.getUserOrRefresh().mapCatching { user ->
        remoteMediaRepository.setMediaItemRating(
            id.value,
            user.favoriteMinRating?.takeIf { favourite } ?: 0)
        remoteMediaItemWorkScheduler.scheduleMediaItemFavourite(id.value, favourite)
    }

    override suspend fun refreshDetailsNowIfMissing(id: MediaId<*>, isVideo: Boolean) : Result<Unit> =
        when (id) {
            is Remote -> refreshRemoteDetailsNowIfMissing(id)
            is Local -> refreshLocalDetailsNowIfMissing(id, isVideo)
            is MediaId.Group -> {
                val remote = id.findRemote?.let {
                    refreshRemoteDetailsNowIfMissing(it)
                } ?: Result.success(Unit)
                val local = id.findLocal?.let {
                    refreshLocalDetailsNowIfMissing(it, isVideo)
                } ?: Result.success(Unit)
                remote + local
            }
        }

    override suspend fun refreshDetailsNow(id: MediaId<*>, isVideo: Boolean) : Result<Unit> =
        when (id) {
            is Remote -> refreshRemoteDetailsNow(id)
            is Local -> refreshLocalDetailsNow(id, isVideo)
            is MediaId.Group -> {
                val remote = id.findRemote?.let {
                    refreshRemoteDetailsNow(it)
                } ?: Result.success(Unit)
                val local = id.findLocal?.let {
                    refreshLocalDetailsNow(it, isVideo)
                } ?: Result.success(Unit)
                remote + local
            }
        }

    private operator fun <T> Result<T>.plus(other: Result<T>) = if (isFailure) this else other

    private suspend fun refreshLocalDetailsNow(id: Local, isVideo: Boolean) =
        localMediaUseCase.refreshLocalMediaItem(id.value, isVideo)

    private suspend fun refreshRemoteDetailsNow(id: Remote) =
        remoteMediaUseCase.refreshDetailsNow(id.value)

    private suspend fun refreshRemoteDetailsNowIfMissing(id: Remote) =
        remoteMediaUseCase.refreshDetailsNowIfMissing(id.value)

    private suspend fun refreshLocalDetailsNowIfMissing(id: Local, isVideo: Boolean) =
        if (localMediaUseCase.getLocalMediaItem(id.value) == null) {
            refreshLocalDetailsNow(id, isVideo)
        } else {
            Result.success(Unit)
        }

    override suspend fun refreshFavouriteMedia() =
        remoteMediaUseCase.refreshFavouriteMedia()

    override fun downloadOriginal(id: MediaId<*>, video: Boolean) {
        if (id is Remote) {
            remoteMediaUseCase.downloadOriginal(id.value, video)
        }
    }

    override fun observeOriginalFileDownloadStatus(id: MediaId<*>): Flow<WorkInfo.State?> =
        when (id) {
            is Remote -> remoteMediaUseCase.observeOriginalFileDownloadStatus(id.value)
            else -> flowOf(WorkInfo.State.SUCCEEDED)
        }

    override fun observeLocalMediaSyncJobStatus(): Flow<WorkInfo.State?> =
        localMediaUseCase.observeLocalMediaSyncJobStatus()

    override suspend fun Group<String, MediaCollectionSource>.toMediaCollection(): List<MediaCollection> {
        val favouriteThreshold = userUseCase.getUserOrRefresh()
            .mapCatching { it.favoriteMinRating!! }
        return items
            .map { (id, source) ->
                mediaCollection(id, source, favouriteThreshold)
            }
            .filter { it.mediaItems.isNotEmpty() }
    }

    override suspend fun List<MediaCollectionSource>.toMediaCollections(): List<MediaCollection> {
        val favouriteThreshold = userUseCase.getUserOrRefresh()
            .mapCatching { it.favoriteMinRating!! }
        return groupBy { dateDisplayer.dateString(it.date) }
            .map { (date, items) ->
                mediaCollection(date, items, favouriteThreshold)
            }
    }

    private fun mediaCollection(
        id: String,
        source: List<MediaCollectionSource>,
        favouriteThreshold: Result<Int>
    ): MediaCollection {
        val albumDate = source.firstOrNull()?.date
        val albumLocation = source.firstOrNull()?.location

        val date = dateDisplayer.dateString(albumDate)
        return MediaCollection(
            id = id,
            displayTitle = date,
            unformattedDate = albumDate,
            location = albumLocation ?: "",
            mediaItems = source.mapNotNull { item ->
                val photoId = item.mediaItemId
                when {
                    photoId.isNullOrBlank() -> null
                    else -> {
                        MediaItemInstance(
                            id = Remote(photoId),
                            mediaHash = photoId,
                            thumbnailUri = with(remoteMediaUseCase) {
                                photoId.toThumbnailUrlFromId()
                            },
                            fullResUri = with(remoteMediaUseCase) {
                                photoId.toFullSizeUrlFromId(item.isVideo)
                            },
                            fallbackColor = item.dominantColor,
                            displayDayDate = date,
                            sortableDate = item.date,
                            isFavourite = favouriteThreshold
                                .map {
                                    (item.rating ?: 0) >= it
                                }
                                .getOrElse { false },
                            ratio = item.aspectRatio ?: 1.0f,
                            isVideo = item.isVideo,
                            syncState = REMOTE_ONLY,
                        )
                    }
                }
            }
        )
    }

    override suspend fun refreshHiddenMedia() =
        remoteMediaUseCase.refreshHiddenMedia()

    override fun trashMediaItem(id: MediaId<*>) {
        if (id is Remote) {
            remoteMediaUseCase.trashMediaItem(id.value)
        }
    }

    override fun deleteMediaItem(id: MediaId<*>) {
        if (id is Remote) {
            remoteMediaUseCase.deleteMediaItem(id.value)
        }
    }

    override fun restoreMediaItem(id: MediaId<*>) {
        if (id is Remote) {
            remoteMediaUseCase.restoreMediaItem(id.value)
        }
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T> =
        userUseCase.getUserOrRefresh().mapCatching {
            action(it.favoriteMinRating!!)
        }
}
