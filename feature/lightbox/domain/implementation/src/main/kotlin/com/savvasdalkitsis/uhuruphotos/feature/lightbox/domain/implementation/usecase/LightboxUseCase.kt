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

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model.LightboxDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.usecase.LightboxUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.implementation.repository.LightboxRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Downloading
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Group
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Local
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Processing
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Remote
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Uploading
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class LightboxUseCase @Inject constructor(
    private val lightboxRepository: LightboxRepository,
) : LightboxUseCase {

    override fun observeLightboxItemDetails(id: MediaId<*>): Flow<LightboxDetails> = with (lightboxRepository) {
        when (id) {
            is Remote -> id.observeDetails()
            is Downloading -> id.remote.observeDetails()
            is Uploading -> id.local.observeDetails()
            is Processing -> id.local.observeDetails()
            is Local -> id.observeDetails()
            is Group -> {
                val localDetails = id.findLocals.map { it.observeDetails() }
                val remoteDetails = id.findRemote?.observeDetails()

                val details = listOfNotNull(*(localDetails + remoteDetails).toTypedArray())
                combine(*details.toTypedArray()) { items ->
                    items.reduce { first, second ->
                        first.mergeWith(second)
                    }
                }
            }
        }
    }

    override suspend fun saveMetadata(id: MediaId<*>, metadata: MediaItemMetadata) = with(lightboxRepository) {
        when (id) {
            is Remote -> saveMetadata(id.value, metadata)
            is Downloading -> saveMetadata(id.remote.value, metadata)
            is Uploading -> saveMetadata(id.local.value.toString(), metadata)
            is Processing -> saveMetadata(id.local.value.toString(), metadata)
            is Local -> saveMetadata(id.value.toString(), metadata)
            is Group -> {
                id.findRemote?.let { saveMetadata(it.value, metadata) }
                id.findLocals.forEach { saveMetadata(it.value.toString(), metadata) }
            }
        }
    }

}

