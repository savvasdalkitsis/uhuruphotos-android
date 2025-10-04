package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization

interface DenormalizationQueue {

    fun newLocalMediaFound(localMediaId: Long)
    fun newRemoteMediaItemFound(remoteMediaId: String)
    fun localMediaQueuedForUpload(localMediaIds: Set<Long>)
    fun uploadingLocalMediaSucceeded(localMediaId: Long)
    fun uploadingLocalMediaFailed(localMediaIds: Set<Long>)
    fun uploadingLocalMedia(localMediaId: Long, percent: Float)
    fun processingUploadedMediaItemSuccess(localMediaId: Long)
    fun processingUploadedMediaItemMessageUpdate(localMediaId: Long, message: String)
    fun processingUploadedMediaItemFailure(localMediaId: Long)
}