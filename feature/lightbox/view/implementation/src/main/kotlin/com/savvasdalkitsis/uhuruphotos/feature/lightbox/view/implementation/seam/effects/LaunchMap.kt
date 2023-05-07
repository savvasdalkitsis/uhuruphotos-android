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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.effects

import android.content.Intent
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffectsContext
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R

data class LaunchMap(val gps: LatLon) : LightboxEffect() {
    context(LightboxEffectsContext) override suspend fun handle() {
        navigator.navigateTo(geoLocation(gps))
    }
    context(LightboxEffectsContext) private fun geoLocation(gps: LatLon) =
        Intent(Intent.ACTION_VIEW, with(gps) {
            "geo:$lat,$lon?q=$lat,$lon(${context.getString(R.string.media)})".uri
        })

    private val String.uri get () = Uri.parse(this)
}