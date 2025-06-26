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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.serialization.Serializable

@Serializable
data class LightboxNavigationRoute(
    val id: MediaIdModel<*>,
    val mediaItemHash: MediaItemHashModel,
    val lightboxSequenceDataSource: LightboxSequenceDataSourceModel = LightboxSequenceDataSourceModel.SingleItemModel,
) : NavigationRoute {

    @IgnoredOnParcel
    override val animateTransitionTo = false

    companion object {
        operator fun invoke(
            mediaItem: MediaItemModel,
            lightboxSequenceDataSource: LightboxSequenceDataSourceModel,
        ) = LightboxNavigationRoute(
            id = mediaItem.id,
            mediaItemHash = mediaItem.mediaHash,
            lightboxSequenceDataSource = lightboxSequenceDataSource
        )
    }
}
