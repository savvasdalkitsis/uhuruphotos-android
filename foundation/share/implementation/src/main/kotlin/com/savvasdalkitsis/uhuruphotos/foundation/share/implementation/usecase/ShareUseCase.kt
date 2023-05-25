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
package com.savvasdalkitsis.uhuruphotos.foundation.share.implementation.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.ImageRequest
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.share.implementation.removeGpsData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class ShareUseCase @Inject constructor(
    private val diskCache: DiskCache,
    private val navigator: Navigator,
    private val imageLoader: ImageLoader,
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
    @ApplicationContext private val context: Context,
) : ShareUseCase {
    private val shareDir = File(context.cacheDir, "share_cache")
    private fun shareFile(postfix: String = "") = File(shareDir, "Photo$postfix.jpg")

    override suspend fun share(url: String) {
        withContext(Dispatchers.IO) {
            url.actualize()?.let { uri ->
                launch("Share Photo", Intent(Intent.ACTION_SEND).apply {
                    setDataAndType(uri, "image/jpeg")
                    putExtra(Intent.EXTRA_STREAM, uri)
                })
            }
        }
    }

    override suspend fun usePhotoAs(url: String) {
        withContext(Dispatchers.IO) {
            url.actualize()?.let { uri ->
                launch("Use as", Intent(Intent.ACTION_ATTACH_DATA).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    setDataAndType(uri, "image/jpeg")
                })
            }
        }
    }

    override suspend fun shareMultiple(urls: List<String>) = withContext(Dispatchers.IO) {
        val uris = urls.realise()
        launch("Share Photos", Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/jpeg"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
        })
    }

    private suspend fun List<String>.realise(): List<Uri> {
        val (contentUris, urls) = partition { it.isContentUri }
        return contentUris.map { it.toUri } + download(*urls.toTypedArray())
    }
    private suspend fun String.actualize() = when {
        isContentUri -> toUri
        else -> download(this).firstOrNull()
    }

    private val String.toUri get() = Uri.parse(this)
    private val String.isContentUri get() = startsWith("content://")

    private suspend fun download(vararg urls: String): ArrayList<out Uri> {
        urls.map { url ->
            imageLoader.enqueue(
                ImageRequest.Builder(context)
                    .data(url)
                    .build()
            ).job
        }.awaitAll()
        return ArrayList(urls.mapIndexedNotNull { index, url ->
            uriFor(url, "-$index")
        })
    }

    private fun launch(text: String, intent: Intent) {
        navigator.navigateTo(
            Intent.createChooser(intent.sharable(), text).sharable()
        )
    }

    private fun Intent.sharable() = apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    private fun uriFor(url: String, postfix: String = "") = diskCache.openSnapshot(url)?.let { snapshot ->
        val data = snapshot.data
        val path = data.toFile().copyTo(shareFile(postfix), overwrite = true)
        snapshot.close()
        if (settingsUseCase.getShareRemoveGpsData()) {
            path.removeGpsData()
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.share.fileprovider",
            path
        )
    }
}
