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
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.Granted
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.RequiresPermissions
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalMediaPermissionsTest {

    private fun state(sdk: Int, vararg granted: String) = LocalMediaPermissions(sdk).state { it in granted }

    @Test
    fun `asks for storage on devices before media location existed`() {
        assertEquals(listOf(READ_EXTERNAL_STORAGE), LocalMediaPermissions(28).requested.toList())
        assertEquals(RequiresPermissions(listOf(READ_EXTERNAL_STORAGE)), state(28))
        assertEquals(Granted, state(28, READ_EXTERNAL_STORAGE))
    }

    @Test
    fun `needs both photos and videos before selected photos existed`() {
        assertEquals(
            listOf(ACCESS_MEDIA_LOCATION, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO),
            LocalMediaPermissions(33).requested.toList(),
        )
        assertEquals(RequiresPermissions(listOf(READ_MEDIA_VIDEO)), state(33, ACCESS_MEDIA_LOCATION, READ_MEDIA_IMAGES))
        assertEquals(Granted, state(33, ACCESS_MEDIA_LOCATION, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
    }

    @Test
    fun `accepts full or selected access to photos`() {
        assertEquals(Granted, state(34, ACCESS_MEDIA_LOCATION, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        assertEquals(Granted, state(34, ACCESS_MEDIA_LOCATION, READ_MEDIA_VISUAL_USER_SELECTED))
    }

    @Test
    fun `still needs the media location with selected photos`() {
        assertEquals(RequiresPermissions(listOf(ACCESS_MEDIA_LOCATION)), state(34, READ_MEDIA_VISUAL_USER_SELECTED))
    }
}
