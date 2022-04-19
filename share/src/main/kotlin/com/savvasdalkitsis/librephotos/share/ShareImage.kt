package com.savvasdalkitsis.librephotos.share

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.ImageRequest
import com.savvasdalkitsis.librephotos.navigation.IntentLauncher
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
    @ApplicationContext private val context: Context,
) {
    private val shareDir = File(context.cacheDir, "share_cache")
    private fun shareFile(postfix: String = "") = File(shareDir, "Photo$postfix.jpg")

    suspend fun share(url: String) = withContext(Dispatchers.IO) {
        download(url).firstOrNull()?.let { uri ->
            launch(Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Share Photo")
                type = "image/jpg"
            })
        }
    }

    suspend fun shareMultiple(urls: List<String>) = withContext(Dispatchers.IO) {
        val uris = download(*urls.toTypedArray())
        launch(Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            putExtra(Intent.EXTRA_TEXT, "Share Photos")
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
            Intent.createChooser(intent, "Share Via")
        )
    }

    private fun uriFor(url: String, postfix: String = "") = diskCache[url]?.let { snapshot ->
        val data = snapshot.data
        val path = data.toFile().copyTo(shareFile(postfix), overwrite = true)
        snapshot.close()
        FileProvider.getUriForFile(
            context,
            "com.savvasdalkitsis.librephotos.share.fileprovider",
            path
        )
    }
}