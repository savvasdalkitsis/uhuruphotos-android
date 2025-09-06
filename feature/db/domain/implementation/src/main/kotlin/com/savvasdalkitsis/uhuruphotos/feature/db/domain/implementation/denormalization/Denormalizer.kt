package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import androidx.core.graphics.toColorInt
import com.github.michaelbull.result.mapOr
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.Denormalization
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.FAVORITE_ADDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.FAVORITE_REMOVED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_LOCAL_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_REMOTE_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_FAILED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.favorites.FavoritesQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.feed.Feed
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.feed.FeedQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
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
    private val userUseCase: UserUseCase,
) {

    suspend fun process(denormalization: Denormalization): Boolean = with(denormalization) {
        when (type) {
            NEW_LOCAL_MEDIA_FOUND -> localMediaItemDetails().processNewLocalMediaFound()
            NEW_REMOTE_MEDIA_FOUND -> remoteMediaItemSummary().processNewRemoteMediaFound()
            FAVORITE_ADDED -> processFavorite(true)
            FAVORITE_REMOVED -> processFavorite(false)
            UPLOADING_LOCAL_MEDIA -> localMediaMd5sum().uploadingLocalMedia()
            UPLOADING_LOCAL_MEDIA_SUCCEEDED -> localMediaMd5sum().uploadingLocalMediaSucceeded()
            UPLOADING_LOCAL_MEDIA_FAILED -> localMediaMd5sum().uploadingLocalMediaFailed()
        }
    }

    private suspend fun Denormalization.localMediaItemDetails(): LocalMediaItemDetails =
        localMediaItemDetailsQueries.getItem(externalId.toLong()).awaitSingle()

    private suspend fun Denormalization.remoteMediaItemSummary(): RemoteMediaItemSummary =
        remoteMediaItemSummaryQueries.get(externalId).awaitSingle()

    private suspend fun LocalMediaItemDetails.processNewLocalMediaFound(
        localSyncStatus: FeedItemSyncStatus = LOCAL_ONLY,
    ): Boolean {
        val md5sum = md5Sum
        val alreadyInFeed = md5sum.existsInFeed()
        val isAlsoRemote = withUserOrFalse { user ->
            val id = MediaItemHashModel(md5sum, user.id).hash
            remoteMediaItemSummaryQueries.isRemote(id).awaitSingle()
        }
        val syncStatus = if (isAlsoRemote) {
            FULLY_SYNCED
        } else {
            localSyncStatus
        }
        if (alreadyInFeed) {
            feedQueries.setUri(FeedUri.local(contentUri), md5sum)
            md5sum.setSyncStatus(syncStatus)
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
        return true
    }

    private suspend fun RemoteMediaItemSummary.processNewRemoteMediaFound(): Boolean =
        withUserOrFalse { user ->
            val md5sum = MediaItemHashModel.fromRemoteMediaHash(
                id,
                user.id
            ).md5
            val alreadyInFeed = md5sum.existsInFeed()
            val isAlsoLocal = localMediaItemDetailsQueries.isLocal(md5sum.value).awaitSingle()
            val syncStatus = if (isAlsoLocal) {
                FULLY_SYNCED
            } else {
                REMOTE_ONLY
            }
            if (alreadyInFeed) {
                md5sum.setSyncStatus(syncStatus)
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
            true
        }

    private suspend fun Md5Hash.uploadingLocalMedia(): Boolean =
        setSyncStatus(LOCAL_UPLOADING)

    private suspend fun Md5Hash.uploadingLocalMediaSucceeded(): Boolean =
        setSyncStatus(FULLY_SYNCED)

    private suspend fun Md5Hash.uploadingLocalMediaFailed(): Boolean =
        setSyncStatus(LOCAL_ONLY)

    private suspend fun Denormalization.processFavorite(isFavorite: Boolean): Boolean {
        if (isFavorite) {
            favoritesQueries.markFavorite(externalId)
        } else {
            favoritesQueries.unmarkFavorite(externalId)
        }.await()
        feedQueries.setFavourite(isFavorite, Md5Hash(externalId)).await()
        return true
    }

    private suspend fun Md5Hash.setSyncStatus(syncStatus: FeedItemSyncStatus): Boolean {
        feedQueries.setSyncStatus(syncStatus, this).await()
        return true
    }

    private suspend fun Denormalization.localMediaMd5sum(): Md5Hash =
        Md5Hash(localMediaItemDetailsQueries.getMd5(externalId.toLong()).awaitSingle())

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
}