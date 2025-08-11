package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueue
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_LOCAL_MEDIA_FOUND
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationType.NEW_REMOTE_MEDIA_FOUND
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
}