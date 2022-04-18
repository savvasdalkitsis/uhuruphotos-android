package com.savvasdalkitsis.librephotos.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import coil.disk.DiskCache
import com.savvasdalkitsis.librephotos.navigation.IntentLauncher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class ShareImage @Inject constructor(
    private val diskCache: DiskCache,
    private val launcher: IntentLauncher,
    @ApplicationContext private val context: Context,
) {
    private val shareDir = File(context.cacheDir, "share_cache")
    private val shareFile = File(shareDir, "Photo.jpg")

    suspend fun share(url: String) = withContext(Dispatchers.IO) {
        diskCache[url]?.let { snapshot ->
            val data = snapshot.data
            val path = data.toFile().copyTo(shareFile, overwrite = true)
            snapshot.close()
            val uri = FileProvider.getUriForFile(
                context,
                "com.savvasdalkitsis.librephotos.share.fileprovider",
                path
            )
            launcher.launch(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_TEXT, "Share Photo")
                        type = "image/jpg"
                    },
                    "Share Via"
                )
            )
        }
    }
}