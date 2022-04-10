package com.savvasdalkitsis.librephotos.photos.viewmodel

import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect.*
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import javax.inject.Inject

class PhotoEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : EffectHandler<PhotoEffect> {

    override fun invoke(effect: PhotoEffect) {
        when (effect) {
            HideSystemBars -> setBars(false)
            ShowSystemBars -> setBars(true)
            NavigateBack -> controllersProvider.navController!!.popBackStack()
        }
    }

    private fun setBars(visible: Boolean) {
        controllersProvider.systemUiController!!.isNavigationBarVisible = visible
    }
}
