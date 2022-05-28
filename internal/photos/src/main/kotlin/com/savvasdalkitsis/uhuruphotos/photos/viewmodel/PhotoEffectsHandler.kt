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
package com.savvasdalkitsis.uhuruphotos.photos.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.CopyToClipboard
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.ErrorRefreshingPeople
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.HideSystemBars
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.LaunchMap
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.SharePhoto
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.ShowSystemBars
import com.savvasdalkitsis.uhuruphotos.share.ShareImage
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PhotoEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val clipboardManager: ClipboardManager,
    private val shareImage: ShareImage,
    private val toaster: Toaster,
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
                toaster.show(R.string.copied_to_clipboard)
            }
            is SharePhoto -> shareImage.share(effect.url)
            is NavigateToPerson -> controllersProvider.navController!!.navigate(
                PersonNavigationTarget.name(effect.id)
            )
            ErrorRefreshingPeople -> toaster.show(R.string.error_refreshing_people)
        }
    }

    private fun geoLocation(gps: LatLon): Intent = Intent(Intent.ACTION_VIEW, with(gps) {
        "geo:$lat,$lon?q=$lat,$lon(${context.getString(R.string.photo)})".uri
    })

    private val String.uri get () = Uri.parse(this)

    private fun setBars(visible: Boolean) {
        controllersProvider.systemUiController!!.isSystemBarsVisible = visible
    }
}
