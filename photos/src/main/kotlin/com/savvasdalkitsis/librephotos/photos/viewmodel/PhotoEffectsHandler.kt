package com.savvasdalkitsis.librephotos.photos.viewmodel

import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect.*
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import javax.inject.Inject

class PhotoEffectsHandler @Inject constructor(
    private val controllersProvider: com.savvasdalkitsis.librephotos.navigation.ControllersProvider,
) : EffectHandler<PhotoEffect> {

    override fun invoke(effect: PhotoEffect) {
        when (effect) {
            HideSystemBars -> setBars(false)
            ShowSystemBars -> setBars(true)
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is LaunchMap -> controllersProvider.intentLauncher.launch(geoLocation(effect.gps))
        }
    }

    private fun geoLocation(gps: LatLng): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:${gps.latitude},${gps.longitude}?q=${gps.latitude},${gps.longitude}(Photo)"))

    private fun setBars(visible: Boolean) {
        controllersProvider.systemUiController!!.isSystemBarsVisible = visible
    }
}
