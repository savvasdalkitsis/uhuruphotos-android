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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.domain.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.latLng
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.log.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.model.toLatLon
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSource
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSource.LOCAL
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSource.REMOTE
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.media.remote.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.api.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.media.remote.repository.RemoteMediaRepository
import com.savvasdalkitsis.uhuruphotos.implementation.media.remote.worker.RemoteMediaItemWorkScheduler
import kotlinx.coroutines.flow.Flow
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

    override fun String.toThumbnailUriFromId(isVideo: Boolean, mediaSource: MediaSource): String =
        when (mediaSource) {
            REMOTE -> with(remoteMediaUseCase) {
                toThumbnailUrlFromId(isVideo)
            }
            LOCAL -> with(localMediaUseCase) {
                toContentUri(isVideo)
            }
        }

    override fun String.toFullSizeUriFromId(isVideo: Boolean, mediaSource: MediaSource): String =
        when (mediaSource) {
            REMOTE -> with(remoteMediaUseCase) {
                toFullSizeUrlFromId(isVideo)
            }
            LOCAL -> with(localMediaUseCase) {
                toContentUri(isVideo)
            }
        }

    override fun observeLocalMedia(): Flow<MediaItemsOnDevice> =
        localMediaUseCase.observeLocalMediaItems().map { mediaItems ->
            when (mediaItems) {
                is LocalMediaItems.Found -> {
                    val primary = listOfNotNull(mediaItems.primaryLocalMediaFolder)
                    val other = mediaItems.localMediaFolders
                    MediaItemsOnDevice.Found((primary + other).map { (folder, items) ->
                        folder to items.map { it.toMediaItem() }
                    })
                }
                is LocalMediaItems.RequiresPermissions -> MediaItemsOnDevice.RequiresPermissions(
                    mediaItems.deniedPermissions
                )
            }
        }

    override fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDevice> =
        localMediaUseCase.observeLocalMediaFolder(albumId).map { mediaItems ->
            when (mediaItems) {
                is LocalFolder.Found -> {
                    MediaFolderOnDevice.Found(
                        mediaItems.bucket.first to mediaItems.bucket.second.map { it.toMediaItem() }
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
                    MediaItem(
                        id = it.id,
                        thumbnailUri = it.id.toThumbnailUrlFromIdNullable(),
                        fullResUri = it.id.toFullSizeUrlFromId(it.isVideo),
                        fallbackColor = it.dominantColor,
                        isFavourite = (it.rating ?: 0) >= threshold,
                        displayDayDate = date,
                        ratio = it.aspectRatio ?: 1f,
                        isVideo = it.isVideo,
                    )
                }
            }
        }

    private fun LocalMediaItem.toMediaItem() = MediaItem(
        id = id.toString(),
        thumbnailUri = contentUri,
        fullResUri = contentUri,
        fallbackColor = fallbackColor,
        isFavourite = false,
        displayDayDate = displayDate,
        ratio = (width / height.toFloat()).takeIf { it > 0 } ?: 1f,
        isVideo = video,
        latLng = latLon,
    )

    override suspend fun getMediaItemDetails(
        id: String,
        mediaSource: MediaSource
    ): MediaItemDetails? = when (mediaSource) {
        REMOTE -> remoteMediaUseCase.getRemoteMediaItemDetails(id)
            ?.toMediaItemDetails()
        LOCAL -> localMediaUseCase.getLocalMediaItem(id.toInt())
            ?.toMediaItemDetails()
    }

    private fun LocalMediaItem.toMediaItemDetails(): MediaItemDetails =
        MediaItemDetails(
            formattedDateAndTime = dateTaken,
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

    override suspend fun setMediaItemFavourite(id: String, favourite: Boolean): Result<Unit> =
        userUseCase.getUserOrRefresh().mapCatching { user ->
            remoteMediaRepository.setMediaItemRating(id, user.favoriteMinRating?.takeIf { favourite } ?: 0)
            remoteMediaItemWorkScheduler.scheduleMediaItemFavourite(id, favourite)
        }

    override suspend fun refreshDetailsNowIfMissing(
        id: String,
        isVideo: Boolean,
        mediaSource: MediaSource
    ) : Result<Unit> = runCatchingWithLog {
        when (mediaSource) {
            REMOTE -> remoteMediaUseCase.refreshDetailsNowIfMissing(id)
            LOCAL -> if (localMediaUseCase.getLocalMediaItem(id.toInt()) == null) {
                localMediaUseCase.refreshLocalMediaItem(id.toInt(), isVideo)
            }
        }
    }

    override suspend fun refreshDetailsNow(
        id: String,
        isVideo: Boolean,
        mediaSource: MediaSource
    ) : Result<Unit> = runCatchingWithLog {
        when (mediaSource) {
            REMOTE -> remoteMediaUseCase.refreshDetailsNow(id)
            LOCAL -> localMediaUseCase.refreshLocalMediaItem(id.toInt(), isVideo)
        }
    }

    override suspend fun refreshFavouriteMedia() {
        remoteMediaUseCase.refreshFavouriteMedia()
    }

    override fun downloadOriginal(id: String, video: Boolean) {
        remoteMediaUseCase.downloadOriginal(id, video)
    }

    override fun observeOriginalFileDownloadStatus(id: String): Flow<WorkInfo.State> =
        remoteMediaUseCase.observeOriginalFileDownloadStatus(id)

    override suspend fun refreshHiddenMedia() {
        remoteMediaUseCase.refreshHiddenMedia()
    }

    override fun trashMediaItem(id: String) {
        remoteMediaUseCase.trashMediaItem(id)
    }

    override fun deleteMediaItem(id: String) {
        remoteMediaUseCase.deleteMediaItem(id)
    }

    override fun restoreMediaItem(id: String) {
        remoteMediaUseCase.restoreMediaItem(id)
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T> =
        userUseCase.getUserOrRefresh().mapCatching {
            action(it.favoriteMinRating!!)
        }
}
