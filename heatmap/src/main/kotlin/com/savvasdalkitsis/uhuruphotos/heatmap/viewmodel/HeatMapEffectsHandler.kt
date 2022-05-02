package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapEffect.*
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class HeatMapEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val toaster: Toaster,
): EffectHandler<HeatMapEffect> {

    override suspend fun invoke(
        effect: HeatMapEffect
    ) {
        when (effect) {
            ErrorLoadingPhotoDetails -> toaster.show("There was an error loading photo details")
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is NavigateToPhoto -> controllersProvider.navController!!.navigate(with(effect) {
                PhotoNavigationTarget.name(photo.id, center, scale, photo.isVideo)
            })
        }
    }

}
