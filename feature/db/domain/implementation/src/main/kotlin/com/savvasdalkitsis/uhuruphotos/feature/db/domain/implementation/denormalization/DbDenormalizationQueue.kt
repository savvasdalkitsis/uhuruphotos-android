package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueue
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_LOCAL_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_REMOTE_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.PROCESSING_UPLOADED_LOCAL_MEDIA_FAILED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.PROCESSING_UPLOADED_LOCAL_MEDIA_MESSAGE_UPDATE
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.PROCESSING_UPLOADED_LOCAL_MEDIA_SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_FAILED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_QUEUED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_SUCCEEDED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DbDenormalizationQueue @Inject constructor(
    private val denormalizationQueries: DenormalizationQueries,
): DenormalizationQueue {

    override fun newLocalMediaFound(localMediaId: Long) {
        insert(NEW_LOCAL_MEDIA_FOUND, localMediaId.toString())
    }

    override fun newRemoteMediaItemFound(remoteMediaId: String) {
        insert(NEW_REMOTE_MEDIA_FOUND, remoteMediaId)
    }

    override fun localMediaQueuedForUpload(localMediaIds: Set<Long>) {
        insert(UPLOADING_LOCAL_MEDIA_QUEUED, localMediaIds)
    }

    override fun uploadingLocalMediaSucceeded(localMediaId: Long) {
        insert(UPLOADING_LOCAL_MEDIA_SUCCEEDED, localMediaId)

    }

    override fun uploadingLocalMediaFailed(localMediaIds: Set<Long>) {
        insert(UPLOADING_LOCAL_MEDIA_FAILED, localMediaIds)
    }

    override fun uploadingLocalMedia(localMediaId: Long, percent: Float) {
        insert(UPLOADING_LOCAL_MEDIA, localMediaId, (percent * 100).toInt().toString())
    }

    override fun processingUploadedMediaItemSuccess(localMediaId: Long) {
        insert(PROCESSING_UPLOADED_LOCAL_MEDIA_SUCCEEDED, localMediaId)
    }

    override fun processingUploadedMediaItemMessageUpdate(localMediaId: Long, message: String) {
        insert(PROCESSING_UPLOADED_LOCAL_MEDIA_MESSAGE_UPDATE, localMediaId, message)
    }

    override fun processingUploadedMediaItemFailure(localMediaId: Long) {
        insert(PROCESSING_UPLOADED_LOCAL_MEDIA_FAILED, localMediaId)
    }

    private fun insert(
        type: DenormalizationType,
        externalId: Any,
        extra: String? = null,
    ) {
        insert(type, setOf(externalId), extra)
    }

    private fun insert(
        type: DenormalizationType,
        externalIds: Set<Any>,
        extra: String? = null,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            externalIds.forEach { externalId ->
                denormalizationQueries.insert(null, type, externalId.toString(), extra).await()
            }
        }
    }
}