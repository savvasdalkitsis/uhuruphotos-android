package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GallerySorting
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class LoadCollage(val id: GalleryId) : GalleryAction() {
    context(GalleryActionsContext) override fun handle(
        state: GalleryState,
        effect: EffectHandler<GalleryEffect>
    ) : Flow<GalleryMutation> {
        sorting = flowSharedPreferences.getEnum(
            "gallerySorting::${id.serializationUniqueId}",
            GallerySorting.default,
        )
        return merge(
            flowOf(GalleryMutation.ChangeCollageDisplay(initialCollageDisplay(id.id))),
            combine(
                galleryDetailsFlow(id.id, effect),
                sorting.asFlow(),
            ) { galleryDetails, sorting ->
                galleryDetails.copy(
                    clusters = when (sorting) {
                        GallerySorting.DATE_DESC -> galleryDetails.clusters.descending()
                        GallerySorting.DATE_ASC -> galleryDetails.clusters.ascending()
                    }
                )
            }.map(GalleryMutation::ShowGallery),
            sorting.asFlow()
                .map { GalleryMutation.ShowGallerySorting(it) },
            loading,
        ).safelyOnStartIgnoring {
            galleryId = id.id
            if (galleryDetailsEmptyCheck(galleryId)) {
                refreshGallery(effect)
            }
        }
    }

    private fun List<Cluster>.descending() = map { cluster ->
        cluster.copy(cels = cluster.cels
            .sortedByDescending {
                it.mediaItem.sortableDate
            }
        )
    }.sortedByDescending {
        it.unformattedDate
    }

    private fun List<Cluster>.ascending() = map { cluster ->
        cluster.copy(cels = cluster.cels
            .sortedBy {
                it.mediaItem.sortableDate
            }
        )
    }.sortedBy {
        it.unformattedDate
    }
}