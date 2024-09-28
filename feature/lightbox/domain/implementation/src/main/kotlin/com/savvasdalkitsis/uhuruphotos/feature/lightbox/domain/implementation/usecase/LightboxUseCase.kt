/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model.LightboxDetailsModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.usecase.LightboxUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.implementation.repository.LightboxRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadataModel
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class LightboxUseCase @Inject constructor(
    private val lightboxRepository: LightboxRepository,
) : LightboxUseCase {

    override fun observeLightboxItemDetails(mediaHash: MediaItemHashModel): Flow<LightboxDetailsModel> =
        lightboxRepository.observeDetails(mediaHash.md5)

    override suspend fun refreshMediaDetails(mediaId: MediaIdModel<*>, mediaHash: MediaItemHashModel) =
        lightboxRepository.refreshDetails(mediaId, mediaHash)

    override fun saveMetadata(mediaHash: MediaItemHashModel, metadata: MediaItemMetadataModel) {
        lightboxRepository.saveMetadata(mediaHash.md5, metadata)
    }
}

