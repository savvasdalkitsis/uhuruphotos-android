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
package com.savvasdalkitsis.uhuruphotos.share

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.ImageRequest
import com.savvasdalkitsis.uhuruphotos.navigation.IntentLauncher
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class ShareImage @Inject constructor(
    private val diskCache: DiskCache,
    private val launcher: IntentLauncher,
    private val imageLoader: ImageLoader,
    private val settingsUseCase: SettingsUseCase,
    @ApplicationContext private val context: Context,
) {
    private val shareDir = File(context.cacheDir, "share_cache")
    private fun shareFile(postfix: String = "") = File(shareDir, "Photo$postfix.jpg")

    suspend fun share(url: String) = withContext(Dispatchers.IO) {
        download(url).firstOrNull()?.let { uri ->
            launch(Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Share Photo")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/jpg"
            })
        }
    }

    suspend fun shareMultiple(urls: List<String>) = withContext(Dispatchers.IO) {
        val uris = download(*urls.toTypedArray())
        launch(Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            putExtra(Intent.EXTRA_TEXT, ArrayList<CharSequence>(listOf("Share Photos" as CharSequence)))
        })
    }

    private suspend fun download(vararg urls: String): ArrayList<out Parcelable> {
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

    private fun launch(intent: Intent) {
        launcher.launch(
            Intent.createChooser(intent, "Share Via").apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    }

    private fun uriFor(url: String, postfix: String = "") = diskCache[url]?.let { snapshot ->
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