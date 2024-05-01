/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.processing.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.Processing
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.ProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.usecase.ProcessingUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProcessingUseCase(
    private val localMediaUseCase: LocalMediaUseCase,
    private val uploadUseCase: UploadUseCase,
) : ProcessingUseCase {

    override fun observeProcessingMedia(): Flow<Processing> = uploadUseCase.observeProcessing().map { ids ->
        Processing(
            ids.mapNotNull { item ->
                localMediaUseCase.getLocalMediaItem(item.id)?.let { mediaItem ->
                    ProcessingItem(
                        localItemId = item.id,
                        displayName = mediaItem.displayName,
                        contentUri = mediaItem.contentUri,
                        error = item.error,
                        lastResponse = item.lastResponse,
                        md5 = mediaItem.md5,
                    )
                }
            }
            .sortedBy(ProcessingItem::displayName)
        )
    }
}
