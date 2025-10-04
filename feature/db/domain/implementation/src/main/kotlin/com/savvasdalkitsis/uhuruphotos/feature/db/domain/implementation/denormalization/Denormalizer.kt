package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import androidx.core.graphics.toColorInt
import com.github.michaelbull.result.mapOr
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.Denormalization
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.FAVORITE_ADDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.FAVORITE_REMOVED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_LOCAL_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_REMOTE_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.PROCESSING_UPLOADED_LOCAL_MEDIA_FAILED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.PROCESSING_UPLOADED_LOCAL_MEDIA_MESSAGE_UPDATE
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.PROCESSING_UPLOADED_LOCAL_MEDIA_SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_FAILED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_QUEUED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.favorites.FavoritesQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.feed.Feed
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.feed.FeedQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.sync.Sync
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.sync.SyncQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.uploads.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.uploads.UploadsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.FULLY_SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.LOCAL_UPLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedUri
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_0
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_180
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_270
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_90
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_UNKNOWN
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.toMediaOrientation
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.FAILED
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.FINISHED
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.IN_QUEUE
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.PROCESSING
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.UPLOADING
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.RemoteUserModel
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateParser
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.LocalMediaDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class Denormalizer @Inject constructor(
    private val localMediaItemDetailsQueries: LocalMediaItemDetailsQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    private val feedQueries: FeedQueries,
    @LocalMediaDateTimeFormat
    private val localMediaDateTimeFormat: DateTimeFormatter,
    @ParsingDateFormat
    private val parsingDateFormat: DateTimeFormatter,
    private val dateParser: DateParser,
    private val favoritesQueries: FavoritesQueries,
    private val syncQueries: SyncQueries,
    private val userUseCase: UserUseCase,
    private val uploadsQueries: UploadsQueries,
) {

    suspend fun process(denormalization: Denormalization): Boolean = with(denormalization) {
        when (type) {
            NEW_LOCAL_MEDIA_FOUND -> localMediaItemDetails().processNewLocalMediaFound()
            NEW_REMOTE_MEDIA_FOUND -> remoteMediaItemSummary()?.processNewRemoteMediaFound() ?: false
            FAVORITE_ADDED -> processFavorite(true)
            FAVORITE_REMOVED -> processFavorite(false)
            UPLOADING_LOCAL_MEDIA -> extra?.toIntOrNull()?.let { percent ->
                localMediaIds().localMediaUploadingPercent(percent)
            } ?: false
            UPLOADING_LOCAL_MEDIA_QUEUED -> localMediaItemDetails().localMediaQueuedForUpload()
            UPLOADING_LOCAL_MEDIA_SUCCEEDED -> localMediaIds().uploadingLocalMediaSucceeded()
            UPLOADING_LOCAL_MEDIA_FAILED -> localMediaIds().uploadingLocalMediaFailed()
            PROCESSING_UPLOADED_LOCAL_MEDIA_SUCCEEDED -> localMediaIds().processingUploadedLocalMediaSucceeded()
            PROCESSING_UPLOADED_LOCAL_MEDIA_MESSAGE_UPDATE -> localMediaIds().processingUploadedLocalMediaMessageUpdated(extra)
            PROCESSING_UPLOADED_LOCAL_MEDIA_FAILED -> localMediaIds().processingUploadedLocalMediaFailed()
        }
    }

    private suspend fun Denormalization.localMediaItemDetails(): LocalMediaItemDetails =
        localMediaItemDetailsQueries.getItem(externalId.toLong()).awaitSingle()

    private suspend fun Denormalization.remoteMediaItemSummary(): RemoteMediaItemSummary? =
        remoteMediaItemSummaryQueries.get(externalId).awaitSingleOrNull()

    private suspend fun LocalMediaItemDetails.processNewLocalMediaFound(
        localSyncStatus: FeedItemSyncStatus = LOCAL_ONLY,
    ): Boolean {
        val md5sum = md5Sum
        val isAlsoRemote = withUserOrFalse { user ->
            val id = MediaItemHashModel(md5sum, user.id).hash
            remoteMediaItemSummaryQueries.isRemote(id).awaitSingle()
        }
        newLocalItemToFeed(isAlsoRemote, localSyncStatus)
        newLocalItemToSync(isAlsoRemote)
        return true
    }

    private suspend fun LocalMediaItemDetails.newLocalItemToFeed(
        isAlsoRemote: Boolean,
        localSyncStatus: FeedItemSyncStatus
    ) {
        val md5sum = md5Sum
        val alreadyInFeed = md5sum.existsInFeed()
        val syncStatus = if (isAlsoRemote) {
            FULLY_SYNCED
        } else {
            localSyncStatus
        }
        if (alreadyInFeed) {
            feedQueries.setUri(FeedUri.local(contentUri), md5sum)
            md5sum.setSyncStatusInFeed(syncStatus)
        } else {
            val date = localMediaDateTimeFormat.parseDateTime(dateTaken)
            val dateString = parsingDateFormat.print(date)
            feedQueries.insert(
                Feed(
                    md5sum = md5sum,
                    day = dateString,
                    sortableValue = timestamp,
                    location = null,
                    uri = FeedUri.local(contentUri),
                    isVideo = video,
                    isFavourite = favoritesQueries.isFavorite(md5).awaitSingle(),
                    syncStatus = syncStatus,
                    fallbackColor = fallbackColor,
                    ratio = ratio,
                )
            )
        }
    }

    private suspend fun LocalMediaItemDetails.newLocalItemToSync(isAlsoRemote: Boolean) = with(syncQueries) {
        if (isAlsoRemote) {
            remove(id)
        } else {
            insert(Sync(
                id = id,
                uri = FeedUri.local(contentUri),
                isVideo = video,
                md5sum = md5Sum,
            ))
        }.await()
    }

    private suspend fun RemoteMediaItemSummary.processNewRemoteMediaFound(): Boolean =
        withUserOrFalse { user ->
            val md5sum = MediaItemHashModel.fromRemoteMediaHash(
                id,
                user.id
            ).md5
            newRemoteItemToFeed(md5sum)
            newRemoteItemToSync(md5sum)
            true
        }

    private suspend fun RemoteMediaItemSummary.newRemoteItemToFeed(
        md5sum: Md5Hash
    ) {
        val alreadyInFeed = md5sum.existsInFeed()
        val isAlsoLocal = localMediaItemDetailsQueries.isLocal(md5sum.value).awaitSingle()
        val syncStatus = if (isAlsoLocal) {
            FULLY_SYNCED
        } else {
            REMOTE_ONLY
        }
        if (alreadyInFeed) {
            md5sum.setSyncStatusInFeed(syncStatus)
        } else {
            val time = dateParser.parseDateOrTimeString(date)
            val dateString = parsingDateFormat.print(time)
            feedQueries.insert(
                Feed(
                    md5sum = md5sum,
                    day = dateString,
                    sortableValue = time?.millis ?: 0,
                    location = location,
                    uri = FeedUri.remote(),
                    isVideo = isVideo,
                    isFavourite = favoritesQueries.isFavorite(md5sum.value).awaitSingle(),
                    syncStatus = syncStatus,
                    fallbackColor = try {
                        dominantColor?.toColorInt()
                    } catch (e: Exception) {
                        log(e) { "Error parsing dominant color $dominantColor" }
                        null
                    },
                    ratio = aspectRatio ?: 1f,
                )
            )
        }
    }

    private suspend fun newRemoteItemToSync(md5sum: Md5Hash) {
        syncQueries.removeByMd5(md5sum).await()
    }

    private suspend fun LocalMediaItemDetails.localMediaQueuedForUpload(): Boolean {
        md5Sum.setSyncStatusInFeed(LOCAL_UPLOADING)
        uploadsQueries.insert(Uploads(
            id = id,
            displayName = displayName,
            uri = FeedUri.local(contentUri),
            md5sum = md5Sum,
            isVideo = video,
            status = IN_QUEUE,
            lastResponse = null,
            progress = null,
        )).await()
        return true
    }

    private suspend fun LocalMediaIds.localMediaUploadingPercent(percent: Int): Boolean {
        uploadsQueries.setStatus(UPLOADING, localMediaId).await()
        uploadsQueries.updateProgress(
            progress = percent.toLong(),
            id = localMediaId,
        ).await()
        return true
    }

    private suspend fun LocalMediaIds.uploadingLocalMediaSucceeded(): Boolean {
        md5Hash.setSyncStatusInFeed(FeedItemSyncStatus.PROCESSING)
        syncQueries.removeByMd5(md5Hash).await()
        uploadsQueries.setStatus(PROCESSING, localMediaId).await()
        return true
    }

    private suspend fun LocalMediaIds.uploadingLocalMediaFailed(): Boolean {
        md5Hash.setSyncStatusInFeed(LOCAL_ONLY)
        uploadsQueries.setStatus(FAILED, localMediaId).await()
        return true
    }

    private suspend fun LocalMediaIds.processingUploadedLocalMediaSucceeded(): Boolean {
        md5Hash.setSyncStatusInFeed(FULLY_SYNCED)
        uploadsQueries.setStatus(FINISHED, localMediaId).await()
        return true
    }

    private suspend fun LocalMediaIds.processingUploadedLocalMediaFailed(): Boolean {
        md5Hash.setSyncStatusInFeed(LOCAL_ONLY)
        uploadsQueries.setStatus(FAILED, localMediaId).await()
        return true
    }

    private suspend fun LocalMediaIds.processingUploadedLocalMediaMessageUpdated(extra: String?): Boolean {
        uploadsQueries.setLastResponse(
            lastResponse = extra,
            id = localMediaId,
        ).await()
        return true
    }

    private suspend fun Denormalization.processFavorite(isFavorite: Boolean): Boolean {
        if (isFavorite) {
            favoritesQueries.markFavorite(externalId)
        } else {
            favoritesQueries.unmarkFavorite(externalId)
        }.await()
        feedQueries.setFavourite(isFavorite, Md5Hash(externalId)).await()
        return true
    }

    private suspend fun Md5Hash.setSyncStatusInFeed(syncStatus: FeedItemSyncStatus): Boolean {
        feedQueries.setSyncStatus(syncStatus, this).await()
        return true
    }

    private suspend fun Denormalization.localMediaIds() = LocalMediaIds(
        externalId.toLong(),
        Md5Hash(localMediaItemDetailsQueries.getMd5(externalId.toLong()).awaitSingle())
    )

    private suspend fun Md5Hash.existsInFeed(): Boolean = feedQueries.hasItem(this).awaitSingle()

    private val LocalMediaItemDetails.md5Sum get() = Md5Hash(md5)

    private suspend fun withUserOrFalse(
        transform: suspend (RemoteUserModel) -> Boolean,
    ) = userUseCase.getRemoteUserOrRefresh().mapOr(false) { transform(it) }

    private val LocalMediaItemDetails.ratio: Float
        get() {
            val (w, h) = when (orientation.toMediaOrientation()) {
                ORIENTATION_UNKNOWN, ORIENTATION_0, ORIENTATION_180 ->
                    width to height
                ORIENTATION_90, ORIENTATION_270 ->
                    height to width
            }
            return (w / h.toFloat()).takeIf { it > 0 } ?: 1f
        }

    private data class LocalMediaIds(val localMediaId: Long, val md5Hash: Md5Hash)
}