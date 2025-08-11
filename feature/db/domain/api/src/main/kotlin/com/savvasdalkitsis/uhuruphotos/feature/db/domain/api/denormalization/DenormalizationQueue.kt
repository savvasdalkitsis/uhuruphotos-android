package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization

interface DenormalizationQueue {

    fun newLocalMediaFound(localMediaId: Long)
    fun newRemoteMediaItemFound(remoteMediaId: String)
}