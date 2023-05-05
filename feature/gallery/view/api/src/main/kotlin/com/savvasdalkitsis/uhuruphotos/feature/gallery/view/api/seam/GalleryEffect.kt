/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId

sealed class GalleryEffect {
    data object NavigateBack : GalleryEffect()
    data object ErrorLoading : GalleryEffect()

    data class OpenLightbox(
        val id: MediaId<*>,
        val lightboxSequenceDataSource: LightboxSequenceDataSource,
    ) : GalleryEffect()

    data class NavigateToPerson(val personId: Int) : GalleryEffect()
}
