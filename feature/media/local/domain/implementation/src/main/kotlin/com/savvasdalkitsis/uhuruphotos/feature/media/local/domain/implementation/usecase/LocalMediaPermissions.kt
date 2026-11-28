/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import android.Manifest.permission.ACCESS_MEDIA_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build.VERSION_CODES.Q
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.Granted
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.RequiresPermissions

internal class LocalMediaPermissions(sdk: Int) {

    private val mediaLocation = listOfNotNull(ACCESS_MEDIA_LOCATION.takeIf { sdk >= Q })
    private val fullAccess = if (sdk >= TIRAMISU) {
        listOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO)
    } else {
        listOf(READ_EXTERNAL_STORAGE)
    }
    private val selectedAccess = listOfNotNull(READ_MEDIA_VISUAL_USER_SELECTED.takeIf { sdk >= UPSIDE_DOWN_CAKE })

    val requested: Array<String> = (mediaLocation + fullAccess + selectedAccess).toTypedArray()

    fun state(isGranted: (String) -> Boolean): LocalPermissions {
        val missing = mediaLocation.filterNot(isGranted) + missingMediaAccess(isGranted)
        return if (missing.isEmpty()) Granted else RequiresPermissions(missing)
    }

    private fun missingMediaAccess(isGranted: (String) -> Boolean): List<String> = when {
        fullAccess.all(isGranted) || selectedAccess.any(isGranted) -> emptyList()
        else -> (fullAccess + selectedAccess).filterNot(isGranted)
    }
}
