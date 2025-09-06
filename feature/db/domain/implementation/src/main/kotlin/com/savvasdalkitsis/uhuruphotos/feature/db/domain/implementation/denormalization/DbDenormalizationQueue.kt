package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueue
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_LOCAL_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_REMOTE_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_FAILED
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.UPLOADING_LOCAL_MEDIA_SUCCEEDED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DbDenormalizationQueue @Inject constructor(
    private val denormalizationQueries: DenormalizationQueries,
): DenormalizationQueue {

    override fun newLocalMediaFound(localMediaId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            denormalizationQueries.insert(null, NEW_LOCAL_MEDIA_FOUND, localMediaId.toString()).await()
        }
    }

    override fun newRemoteMediaItemFound(remoteMediaId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            denormalizationQueries.insert(null, NEW_REMOTE_MEDIA_FOUND, remoteMediaId).await()
        }
    }

    override fun uploadingLocalMedia(localMediaIds: Set<Long>) {
        CoroutineScope(Dispatchers.IO).launch {
            localMediaIds.forEach { localMediaId ->
                denormalizationQueries.insert(null, UPLOADING_LOCAL_MEDIA, localMediaId.toString()).await()
            }
        }
    }

    override fun uploadingLocalMediaSucceeded(localMediaId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            denormalizationQueries.insert(null, UPLOADING_LOCAL_MEDIA_SUCCEEDED, localMediaId.toString()).await()
        }

    }

    override fun uploadingLocalMediaFailed(localMediaIds: Set<Long>) {
        CoroutineScope(Dispatchers.IO).launch {
            localMediaIds.forEach { localMediaId ->
                denormalizationQueries.insert(null, UPLOADING_LOCAL_MEDIA_FAILED, localMediaId.toString()).await()
            }
        }
    }
}