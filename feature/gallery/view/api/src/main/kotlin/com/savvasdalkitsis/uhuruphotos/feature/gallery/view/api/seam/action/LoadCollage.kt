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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GallerySorting
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class LoadCollage(val id: GalleryId) : GalleryAction() {
    context(GalleryActionsContext) override fun handle(
        state: GalleryState,
        effect: EffectHandler<CommonEffect>
    ) : Flow<GalleryMutation> {
        galleryId = id
        return merge(
            flowOf(GalleryMutation.ChangeCollageDisplay(initialCollageDisplay(id.id))),
            combine(
                galleryDetailsFlow(id.id, effect),
                observeSorting(),
            ) { galleryDetails, sorting ->
                galleryDetails.copy(
                    clusters = when (sorting) {
                        GallerySorting.DATE_DESC -> galleryDetails.clusters.descending()
                        GallerySorting.DATE_ASC -> galleryDetails.clusters.ascending()
                    }
                )
            }.map(GalleryMutation::ShowGallery),
            observeSorting()
                .map { GalleryMutation.ShowGallerySorting(it.takeIf { shouldShowSortingAction }) },
            loading,
        ).safelyOnStartIgnoring {
            if (shouldRefreshOnLoad(galleryId.id)) {
                refreshGallery(effect)
            }
        }
    }

    private fun List<Cluster>.descending() = map { cluster ->
        cluster.copy(cels = cluster.cels
            .sortedByDescending {
                it.mediaItem.sortableDate
            }.toPersistentList()
        )
    }.sortedByDescending {
        it.unformattedDate
    }

    private fun List<Cluster>.ascending() = map { cluster ->
        cluster.copy(cels = cluster.cels
            .sortedBy {
                it.mediaItem.sortableDate
            }.toPersistentList()
        )
    }.sortedBy {
        it.unformattedDate
    }
}
