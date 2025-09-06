package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization

interface DenormalizationQueue {

    fun newLocalMediaFound(localMediaId: Long)
    fun newRemoteMediaItemFound(remoteMediaId: String)
    fun uploadingLocalMedia(localMediaIds: Set<Long>)
    fun uploadingLocalMediaSucceeded(localMediaId: Long)
    fun uploadingLocalMediaFailed(localMediaIds: Set<Long>)
}