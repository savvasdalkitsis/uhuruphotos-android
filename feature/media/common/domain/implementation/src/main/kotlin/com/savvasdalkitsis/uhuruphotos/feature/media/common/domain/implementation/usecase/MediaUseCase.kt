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

import androidx.core.graphics.toColorInt
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.combine
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapOr
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDayModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaFolderOnDeviceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.DownloadingIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.LocalIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.ProcessingIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.UploadingIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel.CHANGED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel.SKIPPED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalFolderModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_0
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_180
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_270
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_90
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_UNKNOWN
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.User
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateParser
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simpleOk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.getString
import org.joda.time.DateTime
import se.ansman.dagger.auto.AutoBind
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.month_april_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_august_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_december_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_february_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_january_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_july_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_june_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_march_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_may_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_november_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_october_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_september_short
import javax.inject.Inject

@AutoBind
class MediaUseCase @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val userUseCase: UserUseCase,
    private val dateDisplayer: DateDisplayer,
    private val dateParser: DateParser,
) : MediaUseCase {

    override fun observeLocalMedia(): Flow<MediaItemsOnDeviceModel> =
        combine(
            localMediaUseCase.observeLocalMediaItems(),
            userUseCase.observeUser(),
        ) { localMediaItems, user ->
            combineLocalMediaItemsWithUser(localMediaItems, user)
        }

    private suspend fun combineLocalMediaItemsWithUser(
        localMediaItems: LocalMediaItems,
        user: User
    ) = when (localMediaItems) {
        is LocalMediaItems.Found -> {
            MediaItemsOnDeviceModel.FoundModel(
                primaryFolder = localMediaItems.primaryLocalMediaFolder?.let { (folder, items) ->
                    folder to items.map { it.toMediaItem(user.id) }
                },
                mediaFolders = localMediaItems.localMediaFolders.map { (folder, items) ->
                    folder to items.map { it.toMediaItem(user.id) }
                }
            )
        }

        is LocalMediaItems.RequiresPermissions -> MediaItemsOnDeviceModel.RequiresPermissionsModel(
            localMediaItems.deniedPermissions
        )
    }

    override fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDeviceModel> =
        combine(
            localMediaUseCase.observeLocalMediaFolder(albumId),
            userUseCase.observeUser(),
        ) { mediaItems, user ->
            when (mediaItems) {
                is LocalFolderModel.FoundModel -> {
                    MediaFolderOnDeviceModel.FoundModel(
                        mediaItems.bucket.first to mediaItems.bucket.second.map {
                            it.toMediaItem(user.id)
                        }
                    )
                }
                is LocalFolderModel.RequiresPermissionsModel -> MediaFolderOnDeviceModel.RequiresPermissionsModel(
                    mediaItems.deniedPermissions
                )
                LocalFolderModel.ErrorModel -> MediaFolderOnDeviceModel.ErrorModel
            }
        }

    override fun observeFavouriteMedia(): Flow<Result<List<MediaItemModel>, Throwable>> =
        remoteMediaUseCase.observeFavouriteRemoteMedia()
            .distinctUntilChanged()

    override fun observeHiddenMedia(): Flow<Result<List<MediaItemModel>, Throwable>> =
            remoteMediaUseCase.observeHiddenRemoteMedia()
                .distinctUntilChanged()

    private suspend fun LocalMediaItem.toMediaItem(userId: Int?) = MediaItemInstanceModel(
        id = LocalIdModel(
            id,
            bucket.id,
            video,
            contentUri,
            MediaItemHashModel(md5, userId)
        ),
        mediaHash = MediaItemHashModel(md5, userId),
        fallbackColor = fallbackColor,
        displayDayDate = displayDate,
        sortableDate = sortableDate,
        mediaDay = dateParser.parseDateOrTimeString(sortableDate)?.toMediaDay(),
        isFavourite = false,
        ratio = ratio,
        latLng = latLon,
    )

    private val LocalMediaItem.ratio: Float get() {
        val (w, h) = when (orientation) {
            ORIENTATION_UNKNOWN, ORIENTATION_0, ORIENTATION_180 ->
                width to height
            ORIENTATION_90, ORIENTATION_270 ->
                height to width
        }
        return (w / h.toFloat()).takeIf { it > 0 } ?: 1f
    }

    override suspend fun getFavouriteMediaCount(): Result<Long, Throwable> =
        remoteMediaUseCase.getFavouriteMediaSummariesCount()

    override suspend fun getHiddenMedia(): Result<List<MediaItemModel>, Throwable> =
        remoteMediaUseCase.getHiddenMediaSummaries()

    override suspend fun setMediaItemFavourite(id: MediaIdModel<*>, favourite: Boolean): SimpleResult =
        when (id) {
            is RemoteIdModel -> setRemoteMediaFavourite(id, favourite)
            is MediaIdModel.GroupIdModel -> {
                when (val preferred = id.preferRemote) {
                    is RemoteIdModel -> setRemoteMediaFavourite(preferred, favourite)
                    else -> Ok(Unit)
                }
            }
            else -> simpleOk
        }

    private suspend fun setRemoteMediaFavourite(
        id: RemoteIdModel,
        favourite: Boolean
    ) = remoteMediaUseCase.setMediaItemFavourite(id.value, favourite)

    override suspend fun refreshDetailsNowIfMissing(id: MediaIdModel<*>) : Result<MediaOperationResultModel, Throwable> =
        when (id) {
            is RemoteIdModel -> refreshRemoteDetailsNowIfMissing(id)
            is DownloadingIdModel -> refreshRemoteDetailsNowIfMissing(id.remote)
            is UploadingIdModel -> refreshLocalDetailsNowIfMissing(id.local)
            is ProcessingIdModel -> refreshLocalDetailsNowIfMissing(id.local)
            is LocalIdModel -> refreshLocalDetailsNowIfMissing(id)
            is MediaIdModel.GroupIdModel -> {
                val remote = id.findRemote?.let {
                    refreshRemoteDetailsNowIfMissing(it)
                } ?: Ok(SKIPPED)
                val local = id.findLocals.map {
                    refreshLocalDetailsNowIfMissing(it)
                }
                combine(*(local + remote).toTypedArray()).map {
                    if (it.any { type -> type == CHANGED }) CHANGED else SKIPPED
                }
            }
        }

    override suspend fun refreshDetailsNow(id: MediaIdModel<*>) : SimpleResult =
        when (id) {
            is RemoteIdModel -> refreshRemoteDetailsNow(id)
            is DownloadingIdModel -> refreshRemoteDetailsNow(id.remote)
            is UploadingIdModel -> refreshLocalDetailsNow(id.local)
            is ProcessingIdModel -> refreshLocalDetailsNow(id.local)
            is LocalIdModel -> refreshLocalDetailsNow(id)
            is MediaIdModel.GroupIdModel -> {
                val remote = id.findRemote?.let {
                    refreshRemoteDetailsNow(it)
                } ?: simpleOk
                val local = id.findLocals.map {
                    refreshLocalDetailsNow(it)
                }
                combine(*(local + remote).toTypedArray()).simple()
            }
        }

    private suspend fun refreshLocalDetailsNow(id: LocalIdModel) =
        localMediaUseCase.refreshLocalMediaItem(id.value, id.isVideo)

    private suspend fun refreshRemoteDetailsNow(id: RemoteIdModel) =
        remoteMediaUseCase.refreshDetailsNow(id.value)

    private suspend fun refreshRemoteDetailsNowIfMissing(id: RemoteIdModel) =
        remoteMediaUseCase.refreshDetailsNowIfMissing(id.value)

    private suspend fun refreshLocalDetailsNowIfMissing(id: LocalIdModel): Result<MediaOperationResultModel, Throwable> =
        if (localMediaUseCase.getLocalMediaItem(id.value) == null) {
            refreshLocalDetailsNow(id).map { CHANGED }
        } else {
            Ok(SKIPPED)
        }

    override suspend fun refreshFavouriteMedia() =
        remoteMediaUseCase.refreshFavouriteMedia()

    override suspend fun toMediaCollection(groups: Group<String, MediaCollectionSourceModel>): List<MediaCollectionModel> =
        groups.items
            .mapNotNull { (id, source) ->
                mediaCollection(id, source)
            }
            .filter {
                it.mediaItems.isNotEmpty()
            }

    override suspend fun List<MediaCollectionSourceModel>.toMediaCollections(): List<MediaCollectionModel> =
        groupBy { dateDisplayer.dateString(it.date) }
            .mapNotNull { (date, items) ->
                mediaCollection(date, items)
            }

    private suspend fun mediaCollection(
        id: String,
        source: List<MediaCollectionSourceModel>,
    ): MediaCollectionModel? = userUseCase.getRemoteUserOrRefresh().mapOr(null) { user ->
        val favouriteThreshold = user.favoriteMinRating
        val albumDate = source.firstOrNull()?.date
        val albumLocation = source.firstOrNull()?.location

        val date = dateDisplayer.dateString(albumDate)
        val day = dateParser.parseDateOrTimeString(albumDate)
        MediaCollectionModel(
            id = id,
            displayTitle = date,
            unformattedDate = albumDate,
            location = albumLocation ?: "",
            mediaItems = source.mapNotNull { item ->
                val photoId = item.mediaItemId
                when {
                    photoId.isNullOrBlank() -> null
                    else -> {
                        MediaItemInstanceModel(
                            id = RemoteIdModel(photoId, item.isVideo, MediaItemHashModel.fromRemoteMediaHash(photoId, user.id)),
                            mediaHash = MediaItemHashModel.fromRemoteMediaHash(photoId, user.id),
                            fallbackColor = item.dominantColor?.toColorInt(),
                            displayDayDate = date,
                            sortableDate = item.date,
                            isFavourite = favouriteThreshold != null && (item.rating ?: 0) >= favouriteThreshold,
                            ratio = item.aspectRatio ?: 1.0f,
                            latLng = (item.lat?.toDoubleOrNull() to item.lon?.toDoubleOrNull()).notNull(),
                            mediaDay = day?.toMediaDay(),
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

    override fun trashMediaItem(id: MediaIdModel<*>) {
        if (id is RemoteIdModel) {
            remoteMediaUseCase.trashMediaItem(id.value)
        }
    }

    override fun restoreMediaItem(id: MediaIdModel<*>) {
        if (id is RemoteIdModel) {
            remoteMediaUseCase.restoreMediaItem(id.value)
        }
    }

    private suspend fun DateTime.toMediaDay(): MediaDayModel = MediaDayModel(
        day = dayOfMonth,
        dayOfWeek = dayOfWeek,
        month = monthOfYear,
        year = year,
        monthText = getString(when (monthOfYear) {
            1 -> string.month_january_short
            2 -> string.month_february_short
            3 -> string.month_march_short
            4 -> string.month_april_short
            5 -> string.month_may_short
            6 -> string.month_june_short
            7 -> string.month_july_short
            8 -> string.month_august_short
            9 -> string.month_september_short
            10 -> string.month_october_short
            11 -> string.month_november_short
            else -> string.month_december_short
        })
    )
}
