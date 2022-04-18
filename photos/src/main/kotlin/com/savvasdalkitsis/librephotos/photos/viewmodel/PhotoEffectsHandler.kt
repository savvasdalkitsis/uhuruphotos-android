package com.savvasdalkitsis.librephotos.photos.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect.*
import com.savvasdalkitsis.librephotos.share.ShareImage
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PhotoEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val clipboardManager: ClipboardManager,
    private val shareImage: ShareImage,
    @ApplicationContext private val context: Context,
) : EffectHandler<PhotoEffect> {

    override suspend fun invoke(effect: PhotoEffect) {
        when (effect) {
            HideSystemBars -> setBars(false)
            ShowSystemBars -> setBars(true)
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is LaunchMap -> controllersProvider.intentLauncher.launch(geoLocation(effect.gps))
            is CopyToClipboard -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", effect.content))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_LONG).show()
            }
            is SharePhoto -> shareImage.share(effect.url)
        }
    }

    private fun geoLocation(gps: LatLng): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:${gps.latitude},${gps.longitude}?q=${gps.latitude},${gps.longitude}(Photo)"))

    private fun setBars(visible: Boolean) {
        controllersProvider.systemUiController!!.isSystemBarsVisible = visible
    }
}
