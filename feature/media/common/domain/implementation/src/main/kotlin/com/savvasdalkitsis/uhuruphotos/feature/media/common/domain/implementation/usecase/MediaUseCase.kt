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

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.combine
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.map
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.latLng
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Downloading
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Local
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Remote
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaRefreshResult
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaRefreshResult.SKIPPED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaRefreshResult.REFRESHED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_0
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_180
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_270
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_90
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_UNKNOWN
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.andThenTry
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.toLatLon
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simpleOk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class MediaUseCase @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val serverUseCase: ServerUseCase,
    private val userUseCase: UserUseCase,
    private val dateDisplayer: DateDisplayer,
    private val peopleUseCase: PeopleUseCase,
) : MediaUseCase {

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

    override fun observeFavouriteMedia(): Flow<Result<List<MediaItem>, Throwable>> =
        remoteMediaUseCase.observeFavouriteRemoteMedia()
            .distinctUntilChanged()
            .map { media ->
                media.andThenTry {
                    it.mapToMediaItems().getOrThrow()
                }
            }

    override fun observeHiddenMedia(): Flow<Result<List<MediaItem>, Throwable>> =
            remoteMediaUseCase.observeHiddenRemoteMedia()
                .distinctUntilChanged()
                .mapToPhotos()

    private fun Flow<List<DbRemoteMediaItemSummary>>.mapToPhotos(): Flow<Result<List<MediaItem>, Throwable>> =
        map { it.mapToMediaItems() }

    override suspend fun List<DbRemoteMediaItemSummary>.mapToMediaItems(): Result<List<MediaItem>, Throwable> =
        withFavouriteThreshold { threshold ->
            val serverUrl = serverUseCase.getServerUrl()!!
            map {
                val date = dateDisplayer.dateString(it.date)
                MediaItemInstance(
                    id = Remote(it.id, it.isVideo, serverUrl),
                    mediaHash = it.id,
                    fallbackColor = it.dominantColor,
                    displayDayDate = date,
                    sortableDate = it.date,
                    isFavourite = (it.rating ?: 0) >= threshold,
                    ratio = it.aspectRatio ?: 1f,
                )
            }
        }

    private fun LocalMediaItem.toMediaItem(userId: Int) = MediaItemInstance(
        id = Local(id, video, contentUri, thumbnailPath?.let { "file://$it" } ?: contentUri),
        mediaHash = md5 + userId,
        fallbackColor = fallbackColor,
        displayDayDate = displayDate,
        sortableDate = sortableDate,
        isFavourite = false,
        ratio = ratio,
        latLng = latLon,
    )

    private val LocalMediaItem.ratio: Float get() {
        val (w, h) = when (orientation) {
            ORIENTATION_0, ORIENTATION_180 ->
                width to height
            ORIENTATION_90, ORIENTATION_270 ->
                height to width
            ORIENTATION_UNKNOWN -> 0 to 1
        }
        return (w / h.toFloat()).takeIf { it > 0 } ?: 1f
    }

    override fun observeMediaItemDetails(id: MediaId<*>): Flow<MediaItemDetails> = when (id) {
        is Remote -> id.observeDetails()
        is Downloading -> id.remote.observeDetails()
        is Local -> id.observeDetails()
        is MediaId.Group -> {
            val localDetails = id.findLocal?.observeDetails()
            val remoteDetails = id.findRemote?.observeDetails()

            when {
                remoteDetails != null && localDetails != null ->
                    combine(remoteDetails, localDetails) { (remote, local) ->
                        remote.mergeWith(local)
                    }
                remoteDetails != null && localDetails == null -> remoteDetails
                remoteDetails == null && localDetails != null -> localDetails
                else -> emptyFlow()
            }
        }
    }

    override suspend fun getMediaItemDetails(id: MediaId<*>): MediaItemDetails? = when (id) {
        is Remote -> id.getDetails()
        is Downloading -> id.remote.getDetails()
        is Local -> id.getDetails()
        is MediaId.Group -> {
            val remoteDetails = id.findRemote?.getDetails()
            val localDetails = id.findLocal?.getDetails()
            remoteDetails?.mergeWith(localDetails) ?: localDetails
        }
    }

    private fun Remote.observeDetails() =
        remoteMediaUseCase.observeRemoteMediaItemDetails(value).map {
            it.toMediaItemDetails()
        }

    private suspend fun Remote.getDetails() =
        remoteMediaUseCase.getRemoteMediaItemDetails(value)?.toMediaItemDetails()

    private fun Local.observeDetails() =
        combine(
            localMediaUseCase.observeLocalMediaItem(value),
            userUseCase.observeUser(),
        ) { item, user ->
            item.toMediaItemDetails(user.id)
        }

    private suspend fun Local.getDetails(): MediaItemDetails? =
        userUseCase.getUserOrRefresh().map { user ->
            localMediaUseCase.getLocalMediaItem(value)?.toMediaItemDetails(user.id)
        }.getOr(null)

    private fun LocalMediaItem.toMediaItemDetails(userId: Int): MediaItemDetails =
        MediaItemDetails(
            formattedDateAndTime = displayDateTime,
            isFavourite = false,
            location = "",
            latLon = latLon?.let { (lat, lon) -> LatLon(lat, lon) },
            md5 = md5 + userId,
            localPath = path ?: contentUri,
            peopleInMediaItem = emptyList(),
        )

    private suspend fun DbRemoteMediaItemDetails.toMediaItemDetails(): MediaItemDetails {
        val favouriteThreshold = userUseCase.getUserOrRefresh().getOr(null)?.favoriteMinRating
        val people = peopleUseCase.getPeopleByName().ifEmpty {
            peopleUseCase.refreshPeople()
            peopleUseCase.getPeopleByName()
        }
        val peopleNamesList = peopleNames.orEmpty().deserializePeopleNames
        val serverUrl = serverUseCase.getServerUrl()!!
        val peopleInPhoto = people
            .filter { it.name in peopleNamesList }
            .map {
                it.toPerson { url ->
                    "$serverUrl$url"
                }
            }
        return MediaItemDetails(
            formattedDateAndTime = dateDisplayer.dateTimeString(timestamp),
            isFavourite = favouriteThreshold != null && (rating ?: 0) >= favouriteThreshold,
            location = location.orEmpty(),
            latLon = latLng?.toLatLon,
            md5 = imageHash,
            remotePath = imagePath,
            peopleInMediaItem = peopleInPhoto,
            searchCaptions = captions,
        )
    }

    override suspend fun getFavouriteMediaCount(): Result<Long, Throwable> =
        remoteMediaUseCase.getFavouriteMediaSummariesCount()

    override suspend fun getHiddenMedia(): Result<List<MediaItem>, Throwable> =
        remoteMediaUseCase.getHiddenMediaSummaries().mapToMediaItems()

    override suspend fun setMediaItemFavourite(id: MediaId<*>, favourite: Boolean): SimpleResult =
        when (id) {
            is Remote -> setRemoteMediaFavourite(id, favourite)
            is MediaId.Group -> {
                when (val preferred = id.preferRemote) {
                    is Remote -> setRemoteMediaFavourite(preferred, favourite)
                    else -> Ok(Unit)
                }
            }
            else -> simpleOk
        }

    private suspend fun setRemoteMediaFavourite(
        id: Remote,
        favourite: Boolean
    ) = remoteMediaUseCase.setMediaItemFavourite(id.value, favourite)

    override suspend fun refreshDetailsNowIfMissing(id: MediaId<*>) : Result<MediaRefreshResult, Throwable> =
        when (id) {
            is Remote -> refreshRemoteDetailsNowIfMissing(id)
            is Downloading -> refreshRemoteDetailsNowIfMissing(id.remote)
            is Local -> refreshLocalDetailsNowIfMissing(id)
            is MediaId.Group -> {
                val remote = id.findRemote?.let {
                    refreshRemoteDetailsNowIfMissing(it)
                } ?: Ok(SKIPPED)
                val local = id.findLocal?.let {
                    refreshLocalDetailsNowIfMissing(it)
                } ?: Ok(SKIPPED)
                combine(remote, local).map {
                    if (it.any { type -> type == REFRESHED }) REFRESHED else SKIPPED
                }
            }
        }

    override suspend fun refreshDetailsNow(id: MediaId<*>) : SimpleResult =
        when (id) {
            is Remote -> refreshRemoteDetailsNow(id)
            is Downloading -> refreshRemoteDetailsNow(id.remote)
            is Local -> refreshLocalDetailsNow(id)
            is MediaId.Group -> {
                val remote = id.findRemote?.let {
                    refreshRemoteDetailsNow(it)
                } ?: simpleOk
                val local = id.findLocal?.let {
                    refreshLocalDetailsNow(it)
                } ?: simpleOk
                combine(remote, local).simple()
            }
        }

    private suspend fun refreshLocalDetailsNow(id: Local) =
        localMediaUseCase.refreshLocalMediaItem(id.value, id.isVideo)

    private suspend fun refreshRemoteDetailsNow(id: Remote) =
        remoteMediaUseCase.refreshDetailsNow(id.value)

    private suspend fun refreshRemoteDetailsNowIfMissing(id: Remote) =
        remoteMediaUseCase.refreshDetailsNowIfMissing(id.value)

    private suspend fun refreshLocalDetailsNowIfMissing(id: Local): Result<MediaRefreshResult, Throwable> =
        if (localMediaUseCase.getLocalMediaItem(id.value) == null) {
            refreshLocalDetailsNow(id).map { REFRESHED }
        } else {
            Ok(SKIPPED)
        }

    override suspend fun refreshFavouriteMedia() =
        remoteMediaUseCase.refreshFavouriteMedia()

    override suspend fun Group<String, MediaCollectionSource>.toMediaCollection(): List<MediaCollection> {
        val favouriteThreshold = userUseCase.getUserOrRefresh()
            .andThenTry { it.favoriteMinRating!! }
        return items
            .map { (id, source) ->
                mediaCollection(id, source, favouriteThreshold)
            }
            .filter { it.mediaItems.isNotEmpty() }
    }

    override suspend fun List<MediaCollectionSource>.toMediaCollections(): List<MediaCollection> {
        val favouriteThreshold = userUseCase.getUserOrRefresh()
            .andThenTry { it.favoriteMinRating!! }
        return groupBy { dateDisplayer.dateString(it.date) }
            .map { (date, items) ->
                mediaCollection(date, items, favouriteThreshold)
            }
    }

    private fun mediaCollection(
        id: String,
        source: List<MediaCollectionSource>,
        favouriteThreshold: Result<Int, Throwable>
    ): MediaCollection {
        val albumDate = source.firstOrNull()?.date
        val albumLocation = source.firstOrNull()?.location

        val date = dateDisplayer.dateString(albumDate)
        val serverUrl = serverUseCase.getServerUrl()!!
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
                            id = Remote(photoId, item.isVideo, serverUrl),
                            mediaHash = photoId,
                            fallbackColor = item.dominantColor,
                            displayDayDate = date,
                            sortableDate = item.date,
                            isFavourite = favouriteThreshold
                                .map {
                                    (item.rating ?: 0) >= it
                                }
                                .getOrElse { false },
                            ratio = item.aspectRatio ?: 1.0f,
                            latLng = (item.lat?.toDoubleOrNull() to item.lon?.toDoubleOrNull()).notNull()
                        )
                    }
                }
            }
        )
    }

    private fun <T> Pair<T?, T?>.notNull(): Pair<T, T>? {
        val (k, v) = this
        return when {
            k != null && v != null -> k to v
            else -> null
        }
    }

    override suspend fun refreshHiddenMedia() =
        remoteMediaUseCase.refreshHiddenMedia()

    override fun trashMediaItem(id: MediaId<*>) {
        if (id is Remote) {
            remoteMediaUseCase.trashMediaItem(id.value)
        }
    }

    override fun restoreMediaItem(id: MediaId<*>) {
        if (id is Remote) {
            remoteMediaUseCase.restoreMediaItem(id.value)
        }
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T, Throwable> =
        userUseCase.getUserOrRefresh().andThenTry {
            action(it.favoriteMinRating!!)
        }
}
