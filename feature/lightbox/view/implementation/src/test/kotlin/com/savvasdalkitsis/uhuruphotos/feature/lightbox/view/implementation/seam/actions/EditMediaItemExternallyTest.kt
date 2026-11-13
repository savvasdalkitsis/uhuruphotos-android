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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_EDIT
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowEditOptions
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.LocalIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditMediaItemExternallyTest {

    private val contentUri = "content://media/external/images/media/1"
    private val uri = Uri.parse(contentUri)
    private val packageManager = mockk<PackageManager>()
    private val androidContext = mockk<Context>(relaxed = true) {
        every { contentResolver.getType(uri) } returns "image/heic"
        every { packageManager } returns this@EditMediaItemExternallyTest.packageManager
        every { startActivity(any()) } answers { throw ActivityNotFoundException("No Activity found to handle Intent") }
    }
    private val navigator = mockk<Navigator>(relaxed = true)
    private val context = mockk<LightboxActionsContext>(relaxed = true) {
        every { context } returns androidContext
        every { navigator } returns this@EditMediaItemExternallyTest.navigator
    }
    private val state = mockk<LightboxState> {
        every { currentMediaItem.id } returns LocalIdModel(1, 1, false, contentUri, "", MediaItemHashModel(Md5Hash("hash"), 1))
    }
    private val editor = ResolveInfo().apply {
        activityInfo = ActivityInfo().apply { packageName = "com.editor" }
    }

    @Test
    fun `launching an editor that cannot take the photo does not crash`() = runTest {
        with(EditMediaItemExternally(editor)) { context.handle(state) }.toList()

        val intent = slot<Intent>()
        verify { navigator.navigateTo(capture(intent)) }
        assertEquals(ACTION_EDIT, intent.captured.action)
        assertEquals(uri, intent.captured.data)
        assertEquals("image/heic", intent.captured.type)
        assertEquals("com.editor", intent.captured.`package`)
        assertTrue(intent.captured.flags and FLAG_GRANT_READ_URI_PERMISSION != 0)
    }

    @Test
    fun `lists the editors that can take the photo's actual type`() = runTest {
        val query = slot<Intent>()
        every { packageManager.queryIntentActivities(capture(query), any<PackageManager.ResolveInfoFlags>()) } returns listOf(editor)

        val mutations = with(EditMediaItem) { context.handle(state) }.toList()

        assertEquals(uri, query.captured.data)
        assertEquals("image/heic", query.captured.type)
        assertTrue(mutations.single() is ShowEditOptions)
    }
}
