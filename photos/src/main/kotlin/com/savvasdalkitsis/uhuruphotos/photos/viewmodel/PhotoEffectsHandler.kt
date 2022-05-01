package com.savvasdalkitsis.uhuruphotos.photos.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.*
import com.savvasdalkitsis.uhuruphotos.share.ShareImage
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class PhotoEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val clipboardManager: ClipboardManager,
    private val shareImage: ShareImage,
    private val toaster: Toaster,
) : EffectHandler<PhotoEffect> {

    override suspend fun invoke(effect: PhotoEffect) {
        when (effect) {
            HideSystemBars -> setBars(false)
            ShowSystemBars -> setBars(true)
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is LaunchMap -> controllersProvider.intentLauncher.launch(geoLocation(effect.gps))
            is CopyToClipboard -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", effect.content))
                toaster.show("Copied to clipboard")
            }
            is SharePhoto -> shareImage.share(effect.url)
            is NavigateToPerson -> controllersProvider.navController!!.navigate(
                PersonNavigationTarget.name(effect.id)
            )
            ErrorRefreshingPeople -> toaster.show("Error refreshing people")
        }
    }

    private fun geoLocation(gps: LatLng): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:${gps.latitude},${gps.longitude}?q=${gps.latitude},${gps.longitude}(Photo)"))

    private fun setBars(visible: Boolean) {
        controllersProvider.systemUiController!!.isSystemBarsVisible = visible
    }
}
