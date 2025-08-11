package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import androidx.core.graphics.toColorInt
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapOr
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.Denormalization
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.FAVORITE_ADDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.FAVORITE_REMOVED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_LOCAL_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_REMOTE_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.feed.FeedItemSyncStatusAdapter
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.feed.FeedUri
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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_0
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_180
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_270
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_90
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_UNKNOWN
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.toMediaOrientation
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
    private val serverUseCase: ServerUseCase,
) {

    suspend fun process(denormalization: Denormalization): Boolean = when (denormalization.type) {
        NEW_LOCAL_MEDIA_FOUND -> processNewLocalMediaFound(localMediaItemDetails(denormalization))
        FAVORITE_ADDED -> processFavorite(denormalization.externalId, true)
        FAVORITE_REMOVED -> processFavorite(denormalization.externalId, false)
        NEW_REMOTE_MEDIA_FOUND -> processNewRemoteMediaFound(remoteMediaItemSummary(denormalization))
    }

    private suspend fun localMediaItemDetails(denormalization: Denormalization): LocalMediaItemDetails =
        localMediaItemDetailsQueries.getItem(denormalization.externalId.toLong()).awaitSingle()

    private suspend fun remoteMediaItemSummary(denormalization: Denormalization): RemoteMediaItemSummary =
        remoteMediaItemSummaryQueries.get(denormalization.externalId).awaitSingle()

    private suspend fun processNewLocalMediaFound(localMediaItemDetails: LocalMediaItemDetails): Boolean {
        val existingFeedItem = feedQueries.getItem(Md5Hash(localMediaItemDetails.md5)).awaitSingleOrNull()
        val date = localMediaDateTimeFormat.parseDateTime(localMediaItemDetails.dateTaken)
        val dateString = parsingDateFormat.print(date)
        val isAlsoRemote = userUseCase.getRemoteUserOrRefresh().mapOr(false) { user ->
            val id = MediaItemHashModel(Md5Hash(localMediaItemDetails.md5), user.id).hash
            remoteMediaItemSummaryQueries.isRemote(id).awaitSingle()
        }
        feedQueries.insert(
            existingFeedItem?.copy(
                uri = FeedUri.local(localMediaItemDetails.contentUri),
                syncStatus = if (isAlsoRemote) {
                    FeedItemSyncStatusAdapter.FULLY_SYNCED
                } else {
                    FeedItemSyncStatusAdapter.LOCAL_ONLY
                }
            ) ?: Feed(
                md5sum = Md5Hash(localMediaItemDetails.md5),
                day = dateString,
                sortableValue = localMediaItemDetails.timestamp,
                location = null,
                uri = FeedUri.local(localMediaItemDetails.contentUri),
                isVideo = localMediaItemDetails.video,
                isFavourite = favoritesQueries.isFavorite(localMediaItemDetails.md5).awaitSingle(),
                syncStatus = if (isAlsoRemote) {
                    FeedItemSyncStatusAdapter.FULLY_SYNCED
                } else {
                    FeedItemSyncStatusAdapter.LOCAL_ONLY
                },
                fallbackColor = localMediaItemDetails.fallbackColor,
                ratio = localMediaItemDetails.ratio,
            )
        )
        return true
    }

    private suspend fun processNewRemoteMediaFound(remoteMediaItemSummary: RemoteMediaItemSummary): Boolean {
        return userUseCase.getRemoteUserOrRefresh().mapBoth(
            success = { user ->
                val md5sum = MediaItemHashModel.fromRemoteMediaHash(
                    remoteMediaItemSummary.id,
                    user.id
                ).md5
                val existingFeedItem = feedQueries.getItem(md5sum).awaitSingleOrNull()
                val isAlsoLocal = localMediaItemDetailsQueries.isLocal(md5sum.value).awaitSingle()
                val time = dateParser.parseDateOrTimeString(remoteMediaItemSummary.date)
                val dateString = parsingDateFormat.print(time)
                val serverUrl = serverUseCase.getServerUrl()!!
                feedQueries.insert(
                    existingFeedItem?.copy(
                        syncStatus = if (isAlsoLocal) {
                            FeedItemSyncStatusAdapter.FULLY_SYNCED
                        } else {
                            FeedItemSyncStatusAdapter.REMOTE_ONLY
                        }
                    ) ?: Feed(
                        md5sum = md5sum,
                        day = dateString,
                        sortableValue = time?.millis ?: 0,
                        location = remoteMediaItemSummary.location,
                        uri = FeedUri.remote(),
                        isVideo = remoteMediaItemSummary.isVideo,
                        isFavourite = favoritesQueries.isFavorite(md5sum.value).awaitSingle(),
                        syncStatus = if (isAlsoLocal) {
                            FeedItemSyncStatusAdapter.FULLY_SYNCED
                        } else {
                            FeedItemSyncStatusAdapter.REMOTE_ONLY
                        },
                        fallbackColor = try {
                            remoteMediaItemSummary.dominantColor?.toColorInt()
                        } catch (e: Exception) {
                            log(e) { "Error parsing dominant color ${remoteMediaItemSummary.dominantColor}" }
                            null
                        },
                        ratio = remoteMediaItemSummary.aspectRatio ?: 1f,
                    )
                )
                true
            },
            failure = { false }
        )
    }

    private suspend fun processFavorite(md5: String, isFavorite: Boolean): Boolean {
        if (isFavorite) {
            favoritesQueries.markFavorite(md5)
        } else {
            favoritesQueries.unmarkFavorite(md5)
        }.await()
        return true
    }

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