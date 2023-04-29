package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion

interface LocalMediaDeletionUseCase {

    suspend fun deleteLocalMediaItems(items: List<LocalMediaDeletionRequest>): LocalMediaItemDeletion
}