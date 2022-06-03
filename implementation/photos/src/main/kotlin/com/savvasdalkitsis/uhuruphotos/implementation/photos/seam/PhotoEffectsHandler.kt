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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.seam

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.share.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.person.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.CopyToClipboard
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.ErrorRefreshingPeople
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.HideSystemBars
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.LaunchMap
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.SharePhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.ShowSystemBars
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.api.ui.usecase.UiUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PhotoEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val clipboardManager: ClipboardManager,
    private val shareUseCase: ShareUseCase,
    private val toaster: Toaster,
    private val uiUseCase: UiUseCase,
    @ApplicationContext private val context: Context,
) : EffectHandler<PhotoEffect> {

    override suspend fun handleEffect(effect: PhotoEffect) {
        when (effect) {
            HideSystemBars -> setBars(false)
            ShowSystemBars -> setBars(true)
            NavigateBack -> navigator.navigateBack()
            is LaunchMap -> navigator.navigateTo(geoLocation(effect.gps))
            is CopyToClipboard -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", effect.content))
                toaster.show(R.string.copied_to_clipboard)
            }
            is SharePhoto -> shareUseCase.share(effect.url)
            is NavigateToPerson -> navigator.navigateTo(
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
        uiUseCase.setSystemBarsVisibility(visible)
    }
}
