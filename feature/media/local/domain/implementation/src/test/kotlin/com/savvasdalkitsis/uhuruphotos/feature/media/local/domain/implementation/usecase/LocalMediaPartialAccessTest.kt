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
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.Granted
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import dev.shreyaspatil.permissionFlow.MultiplePermissionState
import dev.shreyaspatil.permissionFlow.PermissionFlow
import dev.shreyaspatil.permissionFlow.PermissionState
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalMediaPartialAccessTest {

    private val app = RuntimeEnvironment.getApplication()
    private val localMediaRepository = mockk<LocalMediaRepository> {
        coJustRun { refresh(any()) }
    }
    private val permissionFlow = mockk<PermissionFlow>()
    private val underTest = LocalMediaUseCase(
        context = app,
        localMediaItemMapper = mockk(),
        localMediaRepository = localMediaRepository,
        permissionFlow = permissionFlow,
        localMediaFolderRepository = mockk(),
        mediaStoreVersionRepository = mockk(relaxed = true),
        workerStatusUseCase = mockk(),
        bitmapUseCase = mockk(),
        localMediaWorkScheduler = mockk(),
    )

    private fun permissionsGranted(vararg granted: String) {
        every { permissionFlow.getMultiplePermissionState(*anyVararg()) } answers {
            val requested = it.invocation.args.single() as Array<*>
            MutableStateFlow(MultiplePermissionState(requested.map { permission ->
                PermissionState(permission as String, permission in granted, false)
            }))
        }
        shadowOf(app).grantPermissions(*granted)
    }

    @Test
    fun `scans the photos the user selected for the app`() = runTest {
        permissionsGranted(READ_MEDIA_VISUAL_USER_SELECTED, ACCESS_MEDIA_LOCATION)

        underTest.refreshAll { _, _ -> }

        coVerify { localMediaRepository.refresh(any()) }
    }

    @Test
    fun `shows the media the user selected for the app`() = runTest {
        permissionsGranted(READ_MEDIA_VISUAL_USER_SELECTED, ACCESS_MEDIA_LOCATION)

        assertEquals(Granted, underTest.observePermissionsState().first())
    }

    @Test
    fun `offers to select photos when asking for media access`() = runTest {
        permissionsGranted()

        assertEquals(
            RequiresPermissions(
                listOf(ACCESS_MEDIA_LOCATION, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED)
            ),
            underTest.observePermissionsState().first(),
        )
    }

    @Test
    fun `declares the selected photos permission`() {
        val permissions = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File("src/main/AndroidManifest.xml"))
            .getElementsByTagName("uses-permission")
        val declared = (0 until permissions.length).map {
            permissions.item(it).attributes.getNamedItem("android:name").nodeValue
        }

        assertEquals(true, READ_MEDIA_VISUAL_USER_SELECTED in declared)
    }
}
